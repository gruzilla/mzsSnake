package xvsm;

import org.mozartspaces.core.Capi;
import org.mozartspaces.core.DefaultMzsCore;

public class XVSMTest {

	/*
	 * stores capi instance
	 */
	private Capi capi;
	
	/*
	 * stores mozartspaces core
	 */
	private DefaultMzsCore core;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new XVSMTest();
	}
	
	public XVSMTest()	{
        // Create an embedded space and construct a Capi instance for it
        core = DefaultMzsCore.newInstance();
        capi = new Capi(core);
	}

}
