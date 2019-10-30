# javaDaemon

***Edit:

Readme.md has not been updated for quite some time now. But it will be soon.The project has moved forward and a demo 
2D tower defense sprite based game has been made, running on both JavaFX and Android with the same code base. Below is the
gif showing the sample. For the implementation glimpse see:
https://github.com/nemanjajelisijevic/javaDaemon/blob/master/game/src/main/java/com/daemonize/game/Game.java

![Alt Text](https://github.com/nemanjajelisijevic/javaDaemon/blob/master/game/assets/towerDefenseDemo.gif)

//Annotation processors : on.

//For javafx usage use oracles jdk (openjdk) or whichever contains the javafx gui libraries.

JavaDaemon is a java library/code generator for creating service objects. Based on the thread per object idea 
(Active Object concurrency pattern).

Generates a wrapper (Daemon) class which is an async representation of an annotated prototype class. Or an interface.
It encapsulates the prototype instance and a thread that executes all the prototype method bodies in its own context, 
queuing the return value to a consuming (calling) thread, allowing that user thread to loop and be responsive.

It maps public methods of the prototype class (annotated @Daemonize) to Daemons methods with similar signature,
differing in one thing. The return value is mapped to an output argument of a type:
   
    public interface Closure<ReturnType> {
      void onReturn(Return<ReturnType> ret);
    }
    
which is implemented and instantiated upon the Daemon method's call, and handed to the selected consumer (event loop)
for execution once the prototype method returns in Daemon threads context.
    
Closure exposes an abstract method onReturn(ReturnType ret) for implementation, which takes the prototype methods 
return value as an argument.

That being said, a Daemon can be called anywhere, but it only returns a Closure to the to the settable
consumer thread (thread stuck in an event loop ie. UI thread).

Underneath, Daemon is a thread that waits on a queue for a called method, or if configured in service 
mode (prototype method annotated with a @SideQuest annotation) constantly executing the sidequest method.

To use the Daemon you'll need two (three actually) java libs: daemonengine and daemonprocessor (requires javapoet).

Daemonengine is a library that holds the threading plumbing needed to run the Daemons.

Daemonprocessor is an annotation processor that generates the Daemon class (.java source file using Javapoet and Java apt 
api) with dependencies to the daemonengine lib, by parsing users prototype class.

Some clarification with an example:

    @Daemon
    public class Example {

        @Daemonize
        public Integer add (Integer i, Integer k) throws InterruptedException {
            //Do the slowest addition the world has ever seen
            Thread.sleep(10000);
            return i + k;
        }

        @Daemonize
        public void dummy(String dummyString, List<Float> floats) {

        }

        public static int subtract(int i, int k) {
            return i - k;
        }

        @Daemonize
        private void shouldNotBeHere(){}

        @Daemonize
        protected Integer shouldNorBeHere() {
            return  1;
        }
        
        @Daemonize
        public List<String> complicated(String text) throws InterruptedException {
            return new ArrayList<>();
        }

        @Daemonize
        public Pair<Integer, String> pairThem() {
            return Pair.create(5, "12");
        }

        @GenerateRunnable
        @Daemonize
        public void voidIt() throws InterruptedException {
            Thread.sleep(2000);
        }

        @Daemonize
        public void voidIt(int a) {}
        
        @Daemonize
        public void voidIt(boolean a) {}
        
        @Daemonize
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

      public ExampleDaemon add(Integer i, Integer k, Closure<Integer> closure) {
        daemonEngine.pursueQuest(new AddMainQuest(i, k, closure));
        return this;
      }

      public ExampleDaemon dummy(String dummystring, List<Float> floats) {
        daemonEngine.pursueQuest(new DummyMainQuest(dummystring, floats));
        return this;
      }

      public ExampleDaemon subtract(int i, int k, Closure<Integer> closure) {
        daemonEngine.pursueQuest(new SubtractMainQuest(i, k, closure));
        return this;
      }

      public ExampleDaemon complicated(String text, Closure<List<String>> closure) {
        daemonEngine.pursueQuest(new ComplicatedMainQuest(text, closure));
        return this;
      }

      public ExampleDaemon pairThem(Closure<Pair<Integer, String>> closure) {
        daemonEngine.pursueQuest(new PairThemMainQuest(closure));
        return this;
      }

      public ExampleDaemon voidIt(Runnable retRun) {
        daemonEngine.pursueQuest(new VoidItMainQuest(retRun));
        return this;
      }

      public ExampleDaemon voidIt(int a) {
        daemonEngine.pursueQuest(new VoidItIMainQuest(a));
        return this;
      }

      public ExampleDaemon voidIt(boolean a) {
        daemonEngine.pursueQuest(new VoidItIIMainQuest(a));
        return this;
      }

      public ExampleDaemon voidIt(boolean a, boolean b, Closure<Boolean> closure) {
        daemonEngine.pursueQuest(new VoidItIIIMainQuest(a, b, closure));
        return this;
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
      public ExampleDaemon start() {
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
    //in Daemon thread's context
    exampleDaemon.add(48, 54, ret -> view.setText(ret.get().toString()));
    
    //or without the lambda syntax:
    exampleDaemon.add(48, 54, new Closure<Integer>() {
      @Override
      public void onReturn(Return<Integer> ret) {
         view.setText(ret.get().toString());
      }
    });
    
    //or maybe we dont want the exception to crash the app:
    exampleDaemon.add(48, 54, ret -> {
      try {
         view.setText(ret.checkAndGet().toString());
         //ret.checkAndGet() throws a checked DaemonException that encapsulates the exception thrown in prototype method
      } catch (DaemonException ex) {
         ex.printStackTrace();
      }
    });
 
//the 'add' call is enqueued to Daemons call queue and returns immediatelty. Closure holding the result is
//handed over to the main loopers queue once the prototype 'add' method returns (10 sec in this case)
 
    exampleDaemon.voidIt(() -> exampleDaemon.add(24, 66, ret -> view.setText(ret.get())));   


To be continued...
