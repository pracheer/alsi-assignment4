package ssm;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class SSM
 * Implements doGet.
 * Map of all sessions created in init (when server loads the servlet).
 * This is synchronized at SessionObject level and hence multiple users with different session ids can work concurrently.
 * 
 */
public class SSM extends HttpServlet {
	private static final String SEPARATOR = "_";

	private static final String COOKIE_NAME = "CS5300SESSION";

	private static final long serialVersionUID = 1L;

	public static final String title = "Session Management";

	HashMap<String, SessionInfo> sessionMap;

	private SSMStub ssmStub;

	private Members members;
	
	private InetAddress myIPAddress;

	private Member me;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SSM() {
		super();
	}

	@Override
	public void init() throws ServletException {
		super.init();
		
		try {
			myIPAddress = InetAddress.getLocalHost();
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		sessionMap = new HashMap<String, SessionInfo>();
		RPCServer rpcServer = new  RPCServer(sessionMap);
		me = new Member(myIPAddress.toString(), rpcServer.getPort());
		
		Thread server = new Thread(rpcServer);
		server.start();
		
		ssmStub = new SSMStub(members);
		
		members = new Members();
		GMClient gmClient = new GMClient(members);
		Thread gmThread = new Thread(gmClient);
		gmThread.start();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			PrintWriter out = response.getWriter();
			response.setContentType("text/html");

			int count = -1;
			Cookie[] cookies = request.getCookies();
			SessionInfo sessionInfo = null;
			
			// First access
			if(cookies == null || cookies.length == 0 ) {
				count = 1;
				sessionInfo = createNewSession(count);
			}
			else {
				// repeated access.
				for (Cookie cookie : cookies) {
					if(cookie.getName().equals(COOKIE_NAME)) {
						String val = cookie.getValue();
						String[] split = val.split(SEPARATOR);
						if(split.length != 3) {
							out.write(handleError("Cookie not constructed properly"));
							return;
						}
						
						String sessionId = split[0];
						int version = Integer.parseInt(split[1]);
						String locationString = split[2];
						
						// consists of ipaddress and port pair.
						Members locationMembers = Members.fromString(locationString);
						
						boolean found = locationMembers.search(me);
						// search in local database.
						if(found) {
							if(sessionMap.containsKey(sessionId)) {
								sessionInfo = sessionMap.get(sessionId);
								if(version != sessionInfo.getVersion()) {
									out.write(handleError("Invalid Version Number"));
									return;
								}
								else if (System.currentTimeMillis() > sessionInfo.getTimestamp()) {
									count = 1;
									sessionInfo = createNewSession(count);
								} 
								else {
									synchronized(sessionInfo) {
										// synchronization on the session object.
										if(request.getParameter("logout")!=null) {
											sessionMap.remove(sessionInfo.getSessionId());
											out.write(createHTML("Bye!"));
											return;
										}
										Value value = sessionInfo.getValue();
										if(request.getParameter("replace")!=null) {
											String message = request.getParameter("message");
											value.setMsg(message);
										}
										count = value.getCount();
										count++;
										value.setCount(count);
										sessionInfo.setValue(value);

										String cookieVal = sessionInfo.getSessionId()+SEPARATOR+sessionInfo.getVersion()+SEPARATOR+sessionInfo.getLocation();
										if(cookieVal.length() > 1024) {
											out.write(handleError("Cookie size exceeded."));
											return;
										}
										Cookie newCookie = new Cookie(COOKIE_NAME, cookieVal); 
										response.addCookie(newCookie);
										out.write(assign3HTML("(" + value.getCount() + ")" + " " + value.getMsg()));
										sessionMap.put(sessionInfo.getSessionId(), sessionInfo);
										// TODO write to W - 1 servers.
										return;
									}
								}
							}
							else {
								count = 1;
								sessionInfo = createNewSession(count);
							}
						}
						
						
						if(!found && sessionId != null ) {
							ssmStub.get(sessionId, version, locations);
							if(sessionMap.containsKey(sessionId)) {
								sessionInfo = sessionMap.get(sessionId);
								if(version != sessionInfo.getVersion()) {
									out.write(handleError("Invalid Version Number"));
									return;
								}
								else if (System.currentTimeMillis() > sessionInfo.getTimestamp()) {
									count = 1;
									sessionInfo = createNewSession(count);
								} 
								else {
									synchronized(sessionInfo) {
										// synchronization on the session object.
										if(request.getParameter("logout")!=null) {
											sessionMap.remove(sessionInfo.getSessionId());
											out.write(createHTML("Bye!"));
											return;
										}
										Value value = sessionInfo.getValue();
										if(request.getParameter("replace")!=null) {
											String message = request.getParameter("message");
											value.setMsg(message);
										}
										count = value.getCount();
										count++;
										value.setCount(count);
										sessionInfo.setValue(value);

										String cookieVal = sessionInfo.getSessionId()+SEPARATOR+sessionInfo.getVersion()+SEPARATOR+sessionInfo.getLocation();
										if(cookieVal.length() > 1024) {
											out.write(handleError("Cookie size exceeded."));
											return;
										}
										Cookie newCookie = new Cookie(COOKIE_NAME, cookieVal); 
										response.addCookie(newCookie);
										out.write(assign3HTML("(" + value.getCount() + ")" + " " + value.getMsg()));
										sessionMap.put(sessionInfo.getSessionId(), sessionInfo);
										return;
									}
								}
							}
							else {
								count = 1;
								sessionInfo = createNewSession(count);
							}
						}
						else {
							if(sessionId == null ) {
								out.write(handleError("Session Id cannot be null. Please check your request"));
								return;
							}
						}
					}
				}

			}

			Cookie cookie = new Cookie(COOKIE_NAME, 
					sessionInfo.getSessionId()+SEPARATOR+sessionInfo.getVersion()+SEPARATOR+sessionInfo.getLocation()); 
			response.addCookie(cookie);
			Value value = sessionInfo.getValue();
			out.write(assign3HTML("(" + value.getCount() + ")" + " " + value.getMsg()));
			sessionMap.put(sessionInfo.getSessionId(), sessionInfo);
		}
		finally {
			// Code to Clean up session that have timed out.
			HashSet<String> removeList = new HashSet<String>();
			
			Set<String> keySet = sessionMap.keySet();
			for (String key : keySet) {
				SessionInfo sessionInfo = sessionMap.get(key);
				if(sessionInfo.getTimestamp() < System.currentTimeMillis()) {
					removeList.add(key);
				}
			}
			
			for (String key : removeList) {
				sessionMap.remove(key);
			}
		}
	}

	private SessionInfo createNewSession(int value) {
		return new SessionInfo(new Value(value));
	}

	private String handleError(String str) {
		StringBuffer buf = new StringBuffer();
		buf.append("An Error has occurred");
		buf.append("<br/><br/>");
		buf.append(str);
		return createHTML(buf.toString());
	}

	private String assign3HTML(String str) {
		StringBuffer buf = new StringBuffer();
		buf.append("<h1>");
		buf.append("<br/><br/>");
		buf.append("<form>");
		buf.append(str);
		buf.append("<br/><br/>");
		buf.append("<input name=\"replace\" type=\"submit\" value=\"Replace\">");
		buf.append("<input name=\"message\" type=\"text\"");
		buf.append("<br/><br/>");
		buf.append("<input name=\"refresh\" type=\"submit\" value=\"Refresh\">");
		buf.append("<br/><br/>");
		buf.append("<input name=\"logout\" type=\"submit\" value=\"Logout\">");
		buf.append("</form>");
		buf.append("</h1>");
		return createHTML(buf.toString());
	}

	private String createHTML(String str) {
		String docType = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 " +
		"Transitional//EN\">\n";
		return docType + "<HTML>\n" +
		"<HEAD><TITLE>" + title + "</TITLE></HEAD>\n" +
		"<BODY>\n" +
		str + 
		"</BODY></HTML>";
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
