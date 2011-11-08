package snake.corso;

import corso.lang.*;
import snake.*;
import snake.data.*;

/**
 * Manages the connection to the corso space.
 * Reads the settings from the file
 * @author Thomas Scheller, Markus Karolus
 */
public class Util
{
	public static final int STRATEGY = CorsoStrategy.PR_DEEP_EAGER |
	CorsoStrategy.RELIABLE_0; /* Reliability class 0 gives us the best performance. */
	public static final int AID = 3;

	private static String cokeSiteLocal;
	private static String cokeSiteServer;
	private static int port;
	private static String username;
	private static String password;
	private static String domain;

	public static boolean usingOneSpace = false; //Helper correct storing sequence
	private static CorsoConnection con;
	private static final String spaceName = "snakeSpace"; //starting point name in the space

	/**
	 * Store the settings from the propertyfile into the local variables
	 * @param newSettings Settings
	 */
	public static void init(Settings newSettings)
	{
		//settings = newSettings;
		cokeSiteLocal = newSettings.getCokeSiteLocal();
		cokeSiteServer = newSettings.getCokeSiteServer();
		port = newSettings.getPort();
		username = newSettings.getUsername();
		password = newSettings.getPassword();
		domain = newSettings.getDomain();
	}

	/**
	 * Connect to "local" corso_coke with the parameters defined in this class.
	 * If an error occurs we throw an Exception.
	 *
	 * @return the connection to our corso_coke
	 */
	public static CorsoConnection getConnection() throws Exception
	{
		if (con == null)
		{
			con = new CorsoConnection();
		}
		try
		{
			if (!con.isConnected())
			{
				Snake.snakeLog.writeLogEntry("Try to connect corsosite: " + cokeSiteLocal + ", Domain: " + domain + ", Port: " + port);
				con.connect(username, password, new CorsoStrategy(STRATEGY), AID, "", cokeSiteLocal, domain, port);
				System.out.println("Connected to: Site: " + cokeSiteLocal + ", Domain: " + domain + ", Port: " + port);
				Snake.snakeLog.writeLogEntry("Connected to corsosite: " + cokeSiteLocal + ", Domain: " + domain + ", Port: " + port);
			}
		}
		catch (Exception e)
		{
			Snake.snakeLog.writeLogEntry("Can't connect to corsosite: " + cokeSiteLocal + ", Domain: " + domain + ", Port: " + port);
			System.err.println("CorsoUtil: Error connecting to corso_coke at " + cokeSiteLocal + ": " +
					e.getMessage());
			throw new Exception("CorsoUtil: Error connecting to corso_coke at " + cokeSiteLocal + ": " +
					e.getMessage());
		}
		return con;
	}

	/**
	 * Disconnect from the "local" corso_coke
	 */
	public static void Disconnect()
	{
		try
		{
			if (con != null)
			{
				con.disconnect();
				con = null;
				Snake.snakeLog.writeLogEntry("Disconnected from Local Space");
			}
		}
		catch (Exception e)
		{

			System.err.println("CorsoUtil: Disconnect from Local Space: " + e.getMessage());
		}
	}

	/**
	 * Connectiontest to another corso site. Read a named variable object
	 * identifier from the other an corso site. If the reading finished without
	 * errors, the spaces are connected, otherwise no connection can't be
	 * established.
	 *
	 * @param siteName String name of the external space (corso_coke)
	 * @param remotePoint SpaceStartPoint
	 * @return boolean
	 */
	private static boolean getConnected(String siteName, SpaceStartPoint remotePoint)
	{
		int waitTime = 10; /*Timeout for connection*/
		try
		{
			System.out.println("Read named varoid from the space");
			Snake.snakeLog.writeLogEntry("Try to read named varoid from the space (space name: "+spaceName+ " / sitename: "+ siteName+")");
			CorsoVarOid temp = con.getNamedVarOid(spaceName,
					siteName,
					null,
					true,
					waitTime);

			// System.out.println("OID gelesen");
			temp.readShareable(remotePoint, null, CorsoConnection.NO_TIMEOUT);
			Snake.snakeLog.writeLogEntry("Successfully read named varoid from the space (space name: "+spaceName+ " / sitename: "+ siteName+")");
			return true;
		}
		catch (Exception err)
		{
			Snake.snakeLog.writeLogEntry("Error occurred while reading named varoid from the space (space name: "+spaceName+ " / sitename: "+ siteName +") ERROR: "+err.toString() );
			System.out.println("CorsoUtil: Error: Reading from the external space: " + err.toString());
			return false;
		}

	}

	/**
	 * Connect to corso_cokes to one large space.
	 * First read the "local" startpoint, then connect to the other space.
	 *
	 * @return SpaceStartPoint Includes the start information for the game
	 */
	public static SpaceStartPoint ConnectSpace()
	{
		Snake.snakeLog.writeLogEntry("Start connection test");
		SpaceStartPoint remotePoint = new SpaceStartPoint(); //Create local startpoint variable
		boolean foundServerSettings = false;

		usingOneSpace = cokeSiteLocal.equals(cokeSiteServer); //using only one corso site?

		foundServerSettings = getConnected(cokeSiteServer, remotePoint); //Search for the startobject on the external server (external corso_coke)
		if (!foundServerSettings)
		{
			try
			{ //Create the startobject on the external server (external corso_coke)
				Snake.snakeLog.writeLogEntry("Try to store startobject on the \"external\" server ("+cokeSiteServer+")");
				CorsoConnection conn2 = new CorsoConnection();
				conn2.connect(username, password, new CorsoStrategy(STRATEGY), AID, "", cokeSiteServer, domain, port); //Connect to the external corso site
				CorsoVarOid newOID = conn2.getOrCreateNamedVarOid(new CorsoStrategy(STRATEGY), spaceName, null); //Create namedVarOIDs
				CorsoVarOid gameListVaroid = conn2.createVarOid(new CorsoStrategy(STRATEGY));
				CorsoVarOid highScoreVaroid = conn2.createVarOid(new CorsoStrategy(STRATEGY));
				newOID.writeShareable(new SpaceStartPoint(cokeSiteServer, gameListVaroid, highScoreVaroid),
						CorsoConnection.INFINITE_TIMEOUT); //Store remote SpaceStartPoint object
				conn2.disconnect(); //Disconnect from the external corso site
				Snake.snakeLog.writeLogEntry("Successfully stored startobject on the \"external\" server ("+cokeSiteServer+")");
			}
			catch (Exception error)
			{
				Snake.snakeLog.writeLogEntry("Error occurred while storing startobject on the \"external\" server ("+cokeSiteServer+") ERROR: "+error.toString() );
				System.out.println("CorsoUtil: Error: Storing data on external corso site" + error.toString());
				return null;
			}
			foundServerSettings = getConnected(cokeSiteServer, remotePoint); //Research for the startobject on the external server (external corso_coke)
		}

		if (!foundServerSettings)
		{
			System.out.println("No connection available to the server ");
			Snake.snakeLog.writeLogEntry("No connection available to the server ("+cokeSiteServer+")");
			return null;
		}

		CorsoVarOid localOID = null;
		try
		{
			Snake.snakeLog.writeLogEntry("Try to get/create startobject-NamedVarOID on \"local\" server ("+cokeSiteLocal+")");
			localOID = con.getOrCreateNamedVarOid(con.getCurrentStrategy(), spaceName, null);
			Snake.snakeLog.writeLogEntry("Successfully got/created startobject-NamedVarOID on \"local\" server ("+cokeSiteLocal+")");
			//System.out.println("localOID:" + localOID.toString());
			try
			{ //Local abspeichern
				Snake.snakeLog.writeLogEntry("Try to store startpoint on \"local\" server ("+cokeSiteLocal+")");
				localOID.writeShareable(remotePoint, CorsoConnection.NO_TIMEOUT); //Save the remotePoint on the local corso site
				Snake.snakeLog.writeLogEntry("Successfully stored startpoint on \"local\" server ("+cokeSiteLocal+")");
				return remotePoint;
			}
			catch (Exception err2)
			{
				Snake.snakeLog.writeLogEntry("Error occurred while storing startpoint on \"local\" server ("+cokeSiteLocal+")");
				try
				{ //Create the startobject on the "local" server (corso_coke)
					Snake.snakeLog.writeLogEntry("Try to create local startpoint on \"local\" server ("+cokeSiteLocal+")");
					CorsoVarOid newGameListVaroid = con.createVarOid(new CorsoStrategy(STRATEGY));
					CorsoVarOid highScoreVaroid = con.createVarOid(new CorsoStrategy(STRATEGY));

					remotePoint = new SpaceStartPoint(remotePoint.getServerName(), newGameListVaroid, highScoreVaroid);
					localOID.writeShareable(remotePoint, CorsoConnection.INFINITE_TIMEOUT); //Store remote SpaceStartPoint object
					Snake.snakeLog.writeLogEntry("Successfully created local startpoint on \"local\" server ("+cokeSiteLocal+")");
					return remotePoint;

				}
				catch (Exception err3)
				{
					Snake.snakeLog.writeLogEntry("Error occurred while creating startpoint on \"local\" server ("+cokeSiteLocal+")");
					System.out.println("CorsoUtil: Error: Can't store local SpaceStartPoint (OID: " + localOID.toString() + ")");
					err3.printStackTrace();
					return null;
				}
			}
		}
		catch (Exception err4)
		{
			Snake.snakeLog.writeLogEntry("Error occurred while getting/creating startobject-NamedVarOID on \"local\" server ("+cokeSiteLocal+")");
			System.out.println("CorsoUtil: Error: Can't create local SpaceStartPoint.");
			err4.printStackTrace();
			return null;
		}
	}

	/**
	 * Creates a variable object if for a corso object.
	 *
	 * @return CorsoVarOid variable objectd id
	 */
	public static CorsoVarOid createVarOid()
	{
		CorsoVarOid oid = null;
		try
		{
			con = getConnection();
			oid = con.createVarOid(new CorsoStrategy(STRATEGY));
		}
		catch (CorsoException e)
		{
			System.err.println("CorsoUtil: CorsoException while creating variable object: " + e.getMessage());
			System.exit( -1);
		}
		catch (Exception e)
		{
			System.err.println("CorsoUtil: Exception while creating variable object: " + e.getMessage());
			System.exit( -1);
		}
		return oid;
	}
}
