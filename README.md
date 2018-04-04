# androidDaemon
async library for android


Generates a wrapper (Daemon) class which is an async representation of an annotated prototype class. Or an interface.
It encapsulates the prototype instance and a thread that executes all the prototype method bodies in its own context,
allowing the main thread to loop and be responsive.

It maps public methods of the prototype class (annotated @Daemonize) to Daemons methods with similar signature,
differing in one thing. The return value is mapped to an output type argument
   
    public interface Closure<ReturnType> {
      void onReturn(Return<ReturnType> ret);
    }
    
which is implemented and instantiated upon the Daemon method's call, and handed to the main looper for execution once 
the prototype method returns.
    
Closure exposes an abstract method onReturn() for implementation, which takes the prototype methods return value as an argument.

That being said, a Daemon can be called anywhere (multiple producers), but it only returns a Closure to the MAIN thread.
For now :)

Underneath, Daemon is a thread that constantly checks a queue for a called method (consumer), or if configured in service 
mode (prototype method annotated with a @SideQuest annotation) constantly executing the sidequest method.

To use the Daemon you'll need two jars: daemonengine.jar and daemonprocessor.jar.

Daemonengine is an android os dependent library that holds the classes needed to run the daemon.

Daemonprocessor is an annotation processor that generates the Daemon class (.java source file using Javapoet and Java apt 
api) with dependencies to the daemonengine lib, by parsing your prototype class.

Some clarification with an example:

    @Daemonize
    public class Example {

        public Integer add (Integer i, Integer k) throws InterruptedException {
            //Do the slowest addition the world has ever seen
            Thread.sleep(10000);
            return i + k;
        }

        public void dummy(String dummyString, List<Float> floats) {

        }

        public static int subtract(int i, int k) {
            return i - k;
        }

        private void shouldNotBeHere(){}

        protected Integer shouldNorBeHere() {
            return  1;
        }

        public List<String> complicated(String text) throws InterruptedException {
            return new ArrayList<>();
        }

        public Pair<Integer, String> pairThem() {
            return Pair.create(5, "12");
        }

        public void voidIt(){}

        public void voidIt(int a) {}

        public void voidIt(boolean a) {}

        public boolean voidIt(boolean a, boolean b) {
            return a & b;
        }
    }

Daemonprocessor will generate the Daemon class in the same package:

    public class ExampleDaemon implements Daemon {

      private Example prototype;

      private MainQuestDaemonEngine daemonEngine = new MainQuestDaemonEngine().setName(this.getClass().getSimpleName());

      //**************************************** CONSTRUCT ******************************************************/

      public ExampleDaemon(Example prototype) {
        this.prototype = prototype;
      }

      //**************************************** PROTOTYPE METHODS MAPPED ***************************************/

      public void add(Integer i, Integer k, Closure<Integer> closure) {
        daemonEngine.pursueQuest(new AddMainQuest(i, k, closure));
      }

      public void dummy(String dummystring, List<Float> floats) {
        daemonEngine.pursueQuest(new DummyMainQuest(dummystring, floats));
      }

      public void subtract(int i, int k, Closure<Integer> closure) {
        daemonEngine.pursueQuest(new SubtractMainQuest(i, k, closure));
      }

      public void complicated(String text, Closure<List<String>> closure) {
        daemonEngine.pursueQuest(new ComplicatedMainQuest(text, closure));
      }

      public void pairThem(Closure<Pair<Integer, String>> closure) {
        daemonEngine.pursueQuest(new PairThemMainQuest(closure));
      }

      public void voidIt() {
        daemonEngine.pursueQuest(new VoidItMainQuest());
      }

      public void voidIt(int a) {
        daemonEngine.pursueQuest(new VoidItIMainQuest(a));
      }

      public void voidIt(boolean a) {
        daemonEngine.pursueQuest(new VoidItIIMainQuest(a));
      }

      public void voidIt(boolean a, boolean b, Closure<Boolean> closure) {
        daemonEngine.pursueQuest(new VoidItIIIMainQuest(a, b, closure));
      }

      //*********************************** DAEMON INTERFACE METHODS ********************************************/

      public Example getPrototype() {
        return prototype;
      }
      
      public ExampleDaemon setPrototype(Example prototype) {
         this.prototype = prototype;
         return this;
      }

      @Override
      public void start() {
        daemonEngine.start();
      }

      @Override
      public void stop() {
        daemonEngine.stop();
      }

      @Override
      public DaemonState getState() {
        return daemonEngine.getState();
      }

      @Override
      public ExampleDaemon setName(String name) {
        daemonEngine.setName(name);
        return this;
      }

      //********************************************************************************************************/

      //...some inner classes needed for method mapping...

    }

So it can be used:

    private TextView view;

    ...

    ExampleDaemon exampleDaemon = new ExampleDaemon(new Example()).setName("exampleDaemon");

    //sweet, sweet lambda as Closure 
    //ret.get() throws a runtime error if an exception has been thrown
    //in daemon thread's context
    exampleDaemon.add(48, 54, ret -> view.setText(ret.get()));
    
    //or without the lambda syntax:
    exampleDaemon.add(48, 54, new Closure<Integer>() {
      @Override
      public void onReturn(Return<Integer> ret) {
         view.setText(ret.get());
      }
    });
    
    //or maybe we dont want the exception to crash the app:
    exampleDaemon.add(48, 54, ret -> {
      try {
         view.setText(ret.checkAndGet().toString()); //ret.checkAndGet() throws a checked exception
      } catch (DaemonException ex) {
         ex.printStackTrace();
      }
    });
    
    //the 'add' call is enqueued to Daemons call queue and returns immediatelty. Closure holding the result is
    //handed over to the main loopers queue once the prototype 'add' method returns

There are three implementations of a Daemon (daemonengine package):
1. MainQuestDaemonEngine
2. SideQuestDaemonEngine
3. HybridDaemonEngine

To be continued...
