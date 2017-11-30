
# java 动态加载class文件技术
 * 1, 传统classloader.
 * 2, [agent技术](http://zheng12tian.iteye.com/blog/1495037). 关键apiVirtualMachine, demo:
 ```java
 public class LiveInjector {

  /**
   * Injects the jar file that contains the code for {@code LiveInjector} as an agent. Its your job to make sure this jar is in fact an agent jar.
   *
   * @throws IllegalStateException If this is not a sun-derived v1.6 VM.
   */
  public void injectSelf() throws IllegalStateException {
    inject(ClassRootFinder.findClassRootOfSelf());
  }

  public boolean isSupportedEnvironment() {
    try {
      Class.forName("com.sun.tools.attach.VirtualMachine");
    } catch (ClassNotFoundException e) {
      return false;
    }

    return true;
  }

  public boolean isInjectable(String jarFile) {
    File f = new File(jarFile);

    return f.isFile();
  }

  /**
   * Injects a jar file into the current VM as a live-loaded agent. The provided jar will be loaded into its own separate class loading context,
   * and its manifest is checked for an {@code Agent-Class} to load. That class should have a static method named {@code agentmain} which will
   * be called, with an {@link java.lang.instrument.Instrumentation} object that you're probably after.
   *
   * @throws IllegalStateException If this is not a sun-derived v1.6 VM.
   */
  public void inject(String jarFile) throws IllegalStateException {
    this.inject(jarFile, Collections.<String, String>emptyMap());
  }

  public void inject(String jarFile, Map<String, String> options) throws IllegalStateException {

    List<String> optionsList = new ArrayList<String>();

    for (Map.Entry<String, String> entry : options.entrySet()) {
      String option = entry.getKey() + "=" + entry.getValue();
      optionsList.add(option);
    }

    String optionString = StringUtils.join(optionsList, ",");

    if (!this.isInjectable(jarFile)) {
      throw new IllegalArgumentException("Live Injection is not possible unless the classpath root to inject is a jar file.");
    }

    injectInternal(jarFile, optionString);
  }

  private void injectInternal(String jarFile, String options) throws IllegalStateException {

    String ownPidS = ManagementFactory.getRuntimeMXBean().getName();
    ownPidS = ownPidS.substring(0, ownPidS.indexOf('@'));
    int ownPid = Integer.parseInt(ownPidS);

    try {
      VirtualMachine vm = VirtualMachine.attach(String.valueOf(ownPid));
      vm.loadAgent(jarFile, options);
    } catch (Throwable exception) {
      throw new IllegalStateException("agent injection not supported on this platform due to unknown reason", exception);
    }
  }
}

 ```