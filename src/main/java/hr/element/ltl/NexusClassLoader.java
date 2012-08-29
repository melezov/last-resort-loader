package hr.element.ltl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class NexusClassLoader {
  private static final File TEMP_DIR =
    new File(System.getProperty("java.io.tmpdir"), "last-resort-loader");

  protected URLConnection open(final URL url) throws IOException {
    return url.openConnection();
  }

  public byte[] fetch(final URL url) throws IOException {
    final URLConnection uC = open(url);
    final InputStream iS = uC.getInputStream();

    try {
      return IOUtils.toByteArray(iS);
    }
    finally {
      iS.close();
    }
  }

  public byte[] fetch(final String url) throws MalformedURLException, IOException {
    return fetch(new URL(url));
  }

  public URLClassLoader getClassLoader(final String url)
      throws MalformedURLException, IOException {
    final String digest = new String(fetch(url + ".sha1"), "UTF-8");
    final File file = new File(TEMP_DIR, digest + ".jar");

    if (!file.exists()) {
      final byte[] body = fetch(url);
      FileUtils.writeByteArrayToFile(file, body);
    }

    return new URLClassLoader(
      new URL[] { file.toURI().toURL() }
    , NexusClassLoader.class.getClassLoader()
    );
  }

  public NexusClassLoader withCredentials(final String username, final String password) {
    return new AuthorizedNexusFetcher(username, password);
  }

  private static class AuthorizedNexusFetcher extends NexusClassLoader {
    private final String username, password;

    public AuthorizedNexusFetcher(final String username, final String password) {
      this.username = username;
      this.password = password;
    }

    private String encodeCredentials() {
      try {
        final String token = username + ':' + password;
        return Base64.encodeBase64String(token.getBytes("ISO-8859-1"));
      }
      catch(final UnsupportedEncodingException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    protected URLConnection open(final URL url) throws IOException {
      final URLConnection uC = super.open(url);
      uC.setRequestProperty("Authorization", "Basic " + encodeCredentials());
      return uC;
    }
  }
}
