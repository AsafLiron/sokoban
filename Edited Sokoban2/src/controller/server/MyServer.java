package controller.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Observable;

public class MyServer extends Observable {

	private int port;
	private ClientHandler ch;
	private volatile boolean stop;
	private boolean in_connction;

	public MyServer(int port, ClientHandler ch) {
		this.port = port;
		this.ch = ch;
		stop = false;
		in_connction = false;
	}

	private void runServer() throws IOException {
		ServerSocket server = new ServerSocket(port);
		server.setSoTimeout(1000);
		while (!stop) {
			try {
				Socket cl = server.accept();
				this.in_connction = true;
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {

							ch.handleClient(cl.getInputStream(), cl.getOutputStream());
							if(stop)
							{

								cl.getInputStream().close();
								cl.getOutputStream().close();
								cl.close();
							}

						} catch (IOException e) {
							System.out.println("invalid I/O of client");
						}
					}
				}).start();

			} catch (SocketTimeoutException e) {

				System.out.println("time out no cliet");

			}

			server.close();
		}
	}

	public void sendToClient(String message) {
		ch.sendToClient(message);
	}

	public void start() {
		new Thread(new Runnable() {
			public void run() {
				try {
					runServer();
				} catch (Exception e) {

				}
			}
		}).start();
	}

	public void stop() {
		if (in_connction) {
			this.ch.sendToClient("end connec");
		}
		stop = true;

	}

	public ClientHandler gethandler() {
		return this.ch;
	}

	public boolean isIn_connction() {
		return in_connction;
	}

	public void setIn_connction(boolean in_connction) {
		this.in_connction = in_connction;
	}

}
