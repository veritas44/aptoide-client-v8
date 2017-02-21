package cm.aptoide.pt.shareapps.socket.file;

import cm.aptoide.pt.shareapps.socket.AptoideServerSocket;
import cm.aptoide.pt.shareapps.socket.AptoideSocket;
import cm.aptoide.pt.shareapps.socket.entities.FileInfo;
import cm.aptoide.pt.shareapps.socket.interfaces.FileServerLifecycle;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by neuro on 27-01-2017.
 */
public class AptoideFileServerSocket<T> extends AptoideServerSocket {

  private final List<FileInfo> fileInfos;
  private boolean startedSending = false;

  private T fileDescriptor;
  private FileServerLifecycle<T> fileServerLifecycle;

  public AptoideFileServerSocket(int port, List<FileInfo> fileInfos, int timeout) {
    super(port, timeout);
    this.fileInfos = fileInfos;
  }

  public AptoideFileServerSocket(int bufferSize, int port, List<FileInfo> fileInfos, int timeout) {
    super(bufferSize, port, timeout);
    this.fileInfos = fileInfos;
  }

  @Override public AptoideSocket start() {
    AptoideSocket start = super.start();
    if (fileServerLifecycle != null) {
      fileServerLifecycle.onFinishSending(fileDescriptor);
    }
    return start;
  }

  @Override protected void onNewClient(Socket socket) {

    if (!startedSending && fileServerLifecycle != null) {
      fileServerLifecycle.onStartSending(fileDescriptor);
      startedSending = true;
    }

    InputStream in = null;

    try {
      for (String filePath : getFilePaths()) {
        System.out.println(Thread.currentThread().getId() + ": Started sending " + filePath);
        in = new FileInputStream(filePath);
        copy(in, socket.getOutputStream(), null);
        System.out.println(Thread.currentThread().getId() + ": Finished sending " + filePath);
        in.close();
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (in != null) {
          in.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private List<String> getFilePaths() {
    List<String> filePaths = new LinkedList<>();

    for (FileInfo fileInfo : fileInfos) {
      filePaths.add(fileInfo.getFilePath());
    }

    return filePaths;
  }

  public AptoideFileServerSocket<T> setFileServerLifecycle(T fileDescriptor,
      FileServerLifecycle<T> fileServerLifecycle) {

    if (fileDescriptor == null) {
      throw new IllegalArgumentException("fileDescriptor cannot be null!");
    }

    if (fileServerLifecycle == null) {
      throw new IllegalArgumentException("fileServerLifecycle cannot be null!");
    }

    this.fileDescriptor = fileDescriptor;
    this.fileServerLifecycle = fileServerLifecycle;

    return this;
  }
}
