package net.filebot.cli;

import static java.nio.charset.StandardCharsets.*;
import static java.util.Arrays.*;
import static java.util.stream.Collectors.*;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import com.google.common.io.ByteStreams;

import net.filebot.Resource;

public class ScriptBundle implements ScriptProvider {

	private Resource<byte[]> bundle;
	private Certificate certificate;

	public ScriptBundle(Resource<byte[]> bundle, InputStream certificate) throws CertificateException {
		this.bundle = bundle.memoize();
		this.certificate = CertificateFactory.getInstance("X.509").generateCertificate(certificate);
	}

	@Override
	public String getScript(String name) throws Exception {
		try (JarInputStream jar = new JarInputStream(new ByteArrayInputStream(bundle.get()), true)) {
			for (JarEntry f = jar.getNextJarEntry(); f != null; f = jar.getNextJarEntry()) {
				if (f.isDirectory() || !f.getName().startsWith(name) || !f.getName().substring(name.length()).equals(".groovy"))
					continue;

				// completely read and verify current jar entry
				byte[] bytes = ByteStreams.toByteArray(jar);
				jar.closeEntry();

				// file must be signed
				Certificate[] certificates = f.getCertificates();

				if (certificates == null || stream(f.getCertificates()).noneMatch(certificate::equals))
					throw new SecurityException(String.format("BAD certificate: %s", Arrays.toString(certificates)));

				return new String(bytes, UTF_8);
			}
		}

		// script does not exist
		throw new FileNotFoundException("Script not found: " + name);
	}

	public Map<String, String> getManifest() throws Exception {
		try (JarInputStream jar = new JarInputStream(new ByteArrayInputStream(bundle.get()), true)) {
			return jar.getManifest().getMainAttributes().entrySet().stream().collect(toMap(it -> it.getKey().toString(), it -> it.getValue().toString()));
		}
	}

}
