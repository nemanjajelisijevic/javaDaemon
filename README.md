# androidDaemon
async library for android


Generates a wrapper (Daemon) class which is an async representation of annotated prototype class. Or interface.
It encapsulates the prototype instance and a thread that executes all the prototype method bodies,
allowing the main thread to loop and be responsive.

It maps public methods of the prototype class (annotated @Daemonize) to Daemons methods with same signature,
except the return type is mapped to an output argument (Closure<ReturnType>) which is instanced upon the Daemon methods call,
and handed to the main looper for execution once the method returns.

That being said, a Daemon can be called anywhere, but it only returns a Closure to MAIN thread.
For now :)

Underneath, Daemon is a thread that constantly checks a queue for a called method (consumer), or if configured in service mode 
(prototype method is annotated with @SideQuest) constantly executing the sidequest method.

To use the Daemon you need two jars: daemonengine.jar and daemonprocessor.jar.
Daemonengine is an android os dependent library that holds the classes needed to run the daemon.
Daemonprocessor is an annotation processor that generates (using Javapoet) the Daemon class (.java source file) with depndencies to
the daemonengine lib, by parsing your prototype class.

Lets clarify this with an example:

    @Daemonize
    public class Example {

        public Integer add (Integer i, Integer k) {
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

    public final class ExampleDaemon implements Daemon {

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

So it can be used from the gui like this:

    private WeakReference<TextView> view;

    ...

    ExampleDaemon exampleDaemon = new ExampleDaemon(new Example());

    exampleDaemon.add(48, 54, new Closure<Integer>() {
        @Override
        public void doTheGuiStuff() {
            try {
                view.get().setText(getResult().toString());
            } catch (DaemonException e) {
                e.printStackTrace();
            }
        }
    });




There are three implementations of a Daemon (daemonengine package):
1. MainQuestDaemonEngine
2. SideQuestDaemonEngine
3. HybridDaemonEngine

To be continued...
