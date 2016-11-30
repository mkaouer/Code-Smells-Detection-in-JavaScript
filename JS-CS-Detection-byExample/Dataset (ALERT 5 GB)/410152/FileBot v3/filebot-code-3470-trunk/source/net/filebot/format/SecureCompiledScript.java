package net.filebot.format;

import java.io.File;
import java.io.FilePermission;
import java.lang.management.ManagementPermission;
import java.lang.reflect.ReflectPermission;
import java.net.SocketPermission;
import java.security.AccessControlContext;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.util.PropertyPermission;

import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import net.filebot.util.ExceptionUtilities;

public class SecureCompiledScript extends CompiledScript {

	public static PermissionCollection getDefaultSandboxPermissions() {
		Permissions permissions = new Permissions();

		// give up on real security, just try to keep files read-only (because of classloading and native lib loading issues)
		permissions.add(new RuntimePermission("createClassLoader"));
		permissions.add(new RuntimePermission("getClassLoader"));
		permissions.add(new RuntimePermission("modifyThread"));
		permissions.add(new RuntimePermission("modifyThreadGroup"));
		permissions.add(new RuntimePermission("loadLibrary.*"));
		permissions.add(new RuntimePermission("accessClassInPackage.*"));
		permissions.add(new RuntimePermission("accessDeclaredMembers"));
		permissions.add(new RuntimePermission("getenv.*"));
		permissions.add(new RuntimePermission("getFileSystemAttributes"));
		permissions.add(new RuntimePermission("readFileDescriptor"));
		permissions.add(new FilePermission("<<ALL FILES>>", "read"));
		permissions.add(new SocketPermission("*", "connect"));
		permissions.add(new PropertyPermission("*", "read"));
		permissions.add(new PropertyPermission("*", "write"));
		permissions.add(new ManagementPermission("monitor"));
		permissions.add(new ReflectPermission("suppressAccessChecks"));
		permissions.add(new ReflectPermission("newProxyInPackage.*"));

		// write permissions for temp and cache folders
		try {
			permissions.add(new FilePermission(new File(System.getProperty("java.io.tmpdir")).getAbsolutePath() + File.separator + "-", "write, delete"));
			permissions.add(new FilePermission(new File(System.getProperty("ehcache.disk.store.dir")).getAbsolutePath() + File.separator + "-", "write, delete"));
		} catch (Exception e) {
			// ignore
		}

		return permissions;
	}

	private final CompiledScript compiledScript;
	private final AccessControlContext sandbox;

	public SecureCompiledScript(CompiledScript compiledScript) {
		this(compiledScript, new AccessControlContext(new ProtectionDomain[] { new ProtectionDomain(null, getDefaultSandboxPermissions()) }));
	}

	public SecureCompiledScript(CompiledScript compiledScript, AccessControlContext sandbox) {
		this.compiledScript = compiledScript;
		this.sandbox = sandbox;
	}

	@Override
	public Object eval(final ScriptContext context) throws ScriptException {
		try {
			return AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {

				@Override
				public Object run() throws ScriptException {
					return compiledScript.eval(context);
				}
			}, sandbox);
		} catch (PrivilegedActionException e) {
			AccessControlException accessException = ExceptionUtilities.findCause(e, AccessControlException.class);

			// try to unwrap AccessControlException
			if (accessException != null)
				throw new ExpressionException(accessException);

			// forward ScriptException
			// e.getException() should be an instance of ScriptException,
			// as only "checked" exceptions will be "wrapped" in a PrivilegedActionException
			throw (ScriptException) e.getException();
		}
	}

	@Override
	public ScriptEngine getEngine() {
		return compiledScript.getEngine();
	}

}
