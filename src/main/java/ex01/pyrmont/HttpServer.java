package ex01.pyrmont;

import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.File;

public class HttpServer {

    /**
     * WEB_ROOT是HTML和其他文件所在的目录。对于这个包，WEB_ROOT是工作目录下的“webroot”目录。
     *
     * 工作目录是文件系统中的位置
     *
     * 从Java命令被调用的地方。
     */
    public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + "webroot";
    // 关闭命令
    private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";
    // 收到关机命令
    private boolean shutdown = false;

    public static void main(String[] args) {
        HttpServer server = new HttpServer();
        server.await();
    }

    public void await() {
        ServerSocket serverSocket = null;
        int port = 8080;
        try {
            serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // 循环等待请求
        while (!shutdown) {
            Socket socket = null;
            InputStream input = null;
            OutputStream output = null;
            try {
                socket = serverSocket.accept();
                input = socket.getInputStream();
                output = socket.getOutputStream();

                //创建请求对象和解析
                Request request = new Request(input);
                request.parse();

                // 创建响应对象
                Response response = new Response(output);
                response.setRequest(request);
                response.sendStaticResource();

                // 关闭socket
                socket.close();

                // 检查前一个uri是否是shutdown命令
                shutdown = request.getUri().equals(SHUTDOWN_COMMAND);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
    }
}
