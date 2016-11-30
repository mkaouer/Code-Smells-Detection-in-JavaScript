/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.classloader;

import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.Logger;

import soot.ArrayType;
import soot.ClassSource;
import soot.CoffiClassSource;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.javaToJimple.IInitialResolver.Dependencies;

/** Loads symbols for SootClasses from either class files or jimple files. */
public class FastClassResolver {
   
  /** Maps each resolved class to a list of all references in it. */
  private final Map<SootClass, ArrayList> classToTypesSignature = new HashMap<SootClass, ArrayList>();

  /** Maps each resolved class to a list of all references in it. */
  private final Map<SootClass, ArrayList> classToTypesHierarchy = new HashMap<SootClass, ArrayList>();

  /** SootClasses waiting to be resolved. */
  @SuppressWarnings("unchecked")
  private final LinkedList<SootClass>[] worklist = new LinkedList[4];

  private String m_tempFolder;
  private List<String> m_classPaths;
  private List<String> m_paths;
  private List<String> m_bodiesClasses;
  private MethodRefFinder m_refFinder;
  private Map<String, Set<String>> m_packageNameCache;
  private PrintWriter m_missingClassLog;
  private MethodFinder m_methodFinder;
  private final Logger m_log;
  private List<String> m_appClasses;
  private Map<String, String> m_filenameToJar;
  
	public FastClassResolver (String temp_folder, List<String> class_paths, List<String> paths, List<String> app_classes) {
    m_log = Logger.getLogger("edu.syr.pcpratts");
    m_appClasses = app_classes;
    worklist[SootClass.HIERARCHY] = new LinkedList<SootClass>();
    worklist[SootClass.SIGNATURES] = new LinkedList<SootClass>();
    worklist[SootClass.BODIES] = new LinkedList<SootClass>();
    m_tempFolder = temp_folder;
    m_classPaths = class_paths;
    m_paths = paths;
    m_refFinder = new MethodRefFinder();
    m_packageNameCache = new HashMap<String, Set<String>>();
    m_methodFinder = new MethodFinder();
    m_filenameToJar = new HashMap<String, String>();
    try {
      m_missingClassLog = new PrintWriter("missing_classes.txt");
    } catch(Exception ex){
      ex.printStackTrace();
    }
  }

  /** Returns a (possibly not yet resolved) SootClass to be used in references
   * to a class. If/when the class is resolved, it will be resolved into this
   * SootClass.
   * */
  public SootClass makeClassRef(String class_name) {
    if(Scene.v().containsClass(class_name))
      return Scene.v().getSootClass(class_name);

    SootClass new_class;
    new_class = new SootClass(class_name);
    new_class.setResolvingLevel(SootClass.DANGLING);
    Scene.v().addClass(new_class);
    if(m_appClasses.contains(class_name)){
      new_class.setApplicationClass();
    }
    
    return new_class;
  }


  /**
   * Resolves the given class. Depending on the resolver settings, may
   * decide to resolve other classes as well. If the class has already
   * been resolved, just returns the class that was already resolved.
   * */
  public SootClass resolveClass(String class_name, int desired_level) {
    SootClass resolved_class = makeClassRef(class_name);
    if(resolved_class.resolvingLevel() >= desired_level){
      return resolved_class;
    }
    addToResolveWorklist(resolved_class, desired_level);
    processResolveWorklist();
    return resolved_class;
  }

  /** Resolve all classes on toResolveWorklist. */
  private void processResolveWorklist() {
    while(true){
      for( int i = SootClass.BODIES; i >= SootClass.HIERARCHY; i-- ) {
        while( !worklist[i].isEmpty() ) {
          SootClass sc = (SootClass) worklist[i].removeFirst();
          if(m_bodiesClasses != null){
            if(m_bodiesClasses.contains(sc.getName()) == false){
              bringToSignatures(sc);
              continue;
            }
          }
          switch(i) {
            case SootClass.BODIES: bringToBodies(sc); break;
            case SootClass.SIGNATURES: bringToSignatures(sc); break;
            case SootClass.HIERARCHY: bringToHierarchy(sc); break;
          }
        }
      }
      if(allEmpty(worklist)){
        return;
      }
    }
  }

  private void addToResolveWorklist(Type type, int level) {
    if( type instanceof RefType )
      addToResolveWorklist(((RefType) type).getClassName(), level);
    else if( type instanceof ArrayType )
      addToResolveWorklist(((ArrayType) type).baseType, level);
  }
  
  private void addToResolveWorklist(String className, int level) {
    addToResolveWorklist(makeClassRef(className), level);
  }
  
  private void addToResolveWorklist(SootClass sc, int desiredLevel) {
    if( sc.resolvingLevel() >= desiredLevel ) {
      return;
    }
    worklist[desiredLevel].add(sc);
  }

  /** Hierarchy - we know the hierarchy of the class and that's it
   * requires at least Hierarchy for all supertypes and enclosing types.
   * */
  private void bringToHierarchy(SootClass sc) {
    if(sc.resolvingLevel() >= SootClass.HIERARCHY ) return;
    sc.setResolvingLevel(SootClass.HIERARCHY);
    
    String className = sc.getName();
    
    List<SootMethod> methods = sc.getMethods();
    for(SootMethod method : methods){
      sc.removeMethod(method);
    }
    
    try {
      InputStream fin = getInputStream(className);
      if(fin == null){
        sc.setPhantom(true);
        return;
      }
      ClassSource is = new CoffiClassSource(className, fin);
    
      Dependencies dependencies = is.resolve(sc);
      classToTypesSignature.put( sc, new ArrayList(dependencies.typesToSignature) );
      classToTypesHierarchy.put( sc, new ArrayList(dependencies.typesToHierarchy) );
      reResolveHierarchy(sc);
    } catch(Exception ex){
      ex.printStackTrace();
    }
  }

  public void reResolveHierarchy(SootClass sc) {
    // Bring superclasses to hierarchy
    if(sc.hasSuperclass()) 
      addToResolveWorklist(sc.getSuperclass(), SootClass.HIERARCHY);
    if(sc.hasOuterClass()) 
      addToResolveWorklist(sc.getOuterClass(), SootClass.HIERARCHY);
    for (SootClass iface : sc.getInterfaces())
      addToResolveWorklist(iface, SootClass.HIERARCHY);
  }

  /** Signatures - we know the signatures of all methods and fields
   * requires at least Hierarchy for all referred to types in these signatures.
   * */
  private void bringToSignatures(SootClass sc) {
    if(sc.resolvingLevel() >= SootClass.SIGNATURES ){ return; }
    bringToHierarchy(sc);
    sc.setResolvingLevel(SootClass.SIGNATURES);
    
    for (SootField f : sc.getFields())
      addToResolveWorklist( f.getType(), SootClass.HIERARCHY );
    for (SootMethod m : sc.getMethods()){
      addToResolveWorklist( m.getReturnType(), SootClass.HIERARCHY );
      for (Type ptype : (List<Type>) m.getParameterTypes())
        addToResolveWorklist( ptype, SootClass.HIERARCHY );
      
      for(SootClass exception : m.getExceptions()) {
        addToResolveWorklist( exception, SootClass.HIERARCHY );
      }
    }

    // Bring superclasses to signatures
    if(sc.hasSuperclass()) 
      addToResolveWorklist(sc.getSuperclass(), SootClass.SIGNATURES);
    for (SootClass iface: sc.getInterfaces())
      addToResolveWorklist(iface, SootClass.SIGNATURES);

  }

  /** Bodies - we can now start loading the bodies of methods
   * for all referred to methods and fields in the bodies, requires
   * signatures for the method receiver and field container, and
   * hierarchy for all other classes referenced in method references.
   * Current implementation does not distinguish between the receiver
   * and other references. Therefore, it is conservative and brings all
   * of them to signatures. But this could/should be improved.
   * */
  private void bringToBodies(SootClass sc) {
    if(sc.resolvingLevel() >= SootClass.BODIES ) return;
    bringToSignatures(sc);
    sc.setResolvingLevel(SootClass.BODIES);
    
    {
      Collection references = classToTypesHierarchy.get(sc);
      if( references == null ) return;

      Iterator it = references.iterator();
      while( it.hasNext() ) {
        final Object o = it.next();
        
        if( o instanceof String ) {
          addToResolveWorklist((String) o, SootClass.HIERARCHY);
        } else if( o instanceof Type ) {
          addToResolveWorklist((Type) o, SootClass.HIERARCHY);
        } else throw new RuntimeException(o.toString());
      }
    }

    {
      Collection references = classToTypesSignature.get(sc);
      if( references == null ) return;

      Iterator it = references.iterator();
      while( it.hasNext() ) {
        final Object o = it.next();

        if( o instanceof String ) {
          addToResolveWorklist((String) o, SootClass.SIGNATURES);
        } else if( o instanceof Type ) {
          addToResolveWorklist((Type) o, SootClass.SIGNATURES);
        } else throw new RuntimeException(o.toString());
      }
    }
 }

 public void reResolve(SootClass cl) {
   int resolvingLevel = cl.resolvingLevel();
   if( resolvingLevel < SootClass.HIERARCHY ) return;
    reResolveHierarchy(cl);
    cl.setResolvingLevel(SootClass.HIERARCHY);
    addToResolveWorklist(cl, resolvingLevel);
    processResolveWorklist();
  }

  private InputStream getInputStream(String className) throws Exception {
    String filename = m_tempFolder+"/"+className.replace(".", File.separator) +".class";
    File file = new File(filename);
    if(file.exists()){
      return new FileInputStream(filename);
    } else {
      //m_log.fine("searching for: "+filename);
      findClass(className);
      file = new File(filename);
      if(file.exists() == false){
        m_missingClassLog.append(className);
        m_missingClassLog.append("\n");
        m_missingClassLog.flush();
        return null;
      }
      return new FileInputStream(filename);
    }
  }

  private boolean findClass(Collection<String> jars, String filename) throws Exception {
    for(String jar : jars){
      JarInputStream fin = new JarInputStream(new FileInputStream(jar));
      while(true){
        JarEntry entry = fin.getNextJarEntry();
        if(entry == null){
          break;
        }
        if(entry.getName().equals(filename) == false){
          continue;
        }
        m_filenameToJar.put(filename, jar);
        WriteJarEntry writer = new WriteJarEntry();
        writer.write(entry, fin, m_tempFolder);
        fin.close();
        return true;
      }
      fin.close();
    }
    return false;
  }
  
  public void findClass(String className) throws Exception {
    String package_name = getPackageName(className);
    String filename = className.replace(".", "/");
    filename += ".class";
    Set<String> jar_cache = m_packageNameCache.get(package_name);
    if(jar_cache == null){
      if(package_name.equals("")){
        findClass(m_classPaths, filename);
        return;
      } else {
        return;
      }
    }
    if(findClass(jar_cache, filename)){
      return;
    }
    //maybe there is a class in the default package, try all if not found in cache.
    if(package_name.equals("")){
      findClass(m_classPaths, filename);
    }
  }

  public void setBodiesClasses(List<String> bodies_classes) {
    m_bodiesClasses = bodies_classes;
  }

  public SootMethod resolveMethod(String curr_signature) {
    return m_methodFinder.find(curr_signature);
  }

  public void cachePackageNames() {
    Set<String> to_cache = new HashSet<String>();
    to_cache.addAll(m_classPaths);
    to_cache.addAll(m_paths);
    String[] to_cache_array = new String[to_cache.size()];
    to_cache_array = to_cache.toArray(to_cache_array);
    Arrays.sort(to_cache_array);
    for(String jar : to_cache_array){
      if(jar.endsWith(".jar")){
        m_log.fine("caching package names for: "+jar);
        try {
          JarInputStream jin = new JarInputStream(new FileInputStream(jar));
          while(true){
            JarEntry entry = jin.getNextJarEntry();
            if(entry == null){
              break;
            }
            String name = entry.getName();
            if(name.endsWith(".class")){
              name = name.replace(".class", "");
              name = name.replace("/", ".");
              name = getPackageName(name);
            } else {
              name = name.replace("/", ".");
              name = name.substring(0, name.length()-1);
            }
            if(m_packageNameCache.containsKey(name)){
              Set<String> jars = m_packageNameCache.get(name);
              if(jars.contains(jar) == false){
                jars.add(jar);
              }
            } else {
              Set<String> jars = new HashSet<String>();
              jars.add(jar);
              m_packageNameCache.put(name, jars);
            }
          }
          jin.close();
        } catch(Exception ex){
          ex.printStackTrace();
        }
      }
    }
  }

  private String getPackageName(String className) {
    String[] tokens = className.split("\\.");
    String ret = "";
    for(int i = 0; i < tokens.length - 1; ++i){
      ret += tokens[i];
      if(i < tokens.length - 2){
        ret += ".";
      }
    }
    return ret;
  }

  public SootClass forceResolveClass(String name, int level) {
    m_bodiesClasses.add(name);
    return resolveClass(name, level);
  }

  public void clearBodyClasses() {
    m_bodiesClasses = null;
  }

  private boolean allEmpty(LinkedList<SootClass>[] worklist) {
    for(LinkedList<SootClass> sub_list : worklist){
      if(sub_list == null){
        continue;
      }
      if(sub_list.isEmpty() == false){
        return false;
      }
    }
    return true;
  }

  public String getJarNameForFilename(String filename, String class_name) {
    if(m_filenameToJar.containsKey(filename) == false){
      try {
        findClass(class_name);
      } catch(Exception ex){
        //ignore
      }
    }
    return m_filenameToJar.get(filename);
  }
}
