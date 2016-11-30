/*
 * Created on 23-Sep-2004
 *
 * Merchant of Venice - technical analysis software for the stock market.
 * Copyright (C) 2002 Andrew Leppard (aleppard@picknowl.com.au)
 * This portion of code Copyright (C) 2004 Dan Makovec (venice@makovec.net)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA 
*/

package nz.org.venice.macro;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.lang.NoClassDefFoundError;
import java.lang.reflect.Array;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JOptionPane;

import nz.org.venice.prefs.PreferencesManager;
import nz.org.venice.ui.DesktopManager;
import nz.org.venice.util.Locale;

/**
 * @author Dan Makovec venice@makovec.net
 *
 * This class handles parsing and execution of macros
 */
public class MacroManager {
    
    private static Hashtable err_output = new Hashtable();
    private static Hashtable out_output   = new Hashtable();
    
    private static Hashtable compiled_macros = new Hashtable();
    
    /**
     * Removes the compiled macro from memory.  Useful for when editing
     * macro source, so that changes are automatically compiled in
     * 
     * @param m The macro to uncache
     */
    public static void uncacheCompiledMacro(StoredMacro m) {
        compiled_macros.remove(m.getName());
    }
    
    /**
     * Execute all the macros that have been configured to be run on startup,
     * in the order that they have been assigned.
     */
    
    public static void executeStartupMacros() {
        List macros = PreferencesManager.getStoredMacros();
        Object array_list = Array.newInstance(StoredMacro.class, macros.size());

        // Set up the start sequence
        for (int i = 0; i < macros.size(); i++) {
            if ((((StoredMacro) macros.get(i)).isOn_startup()) &&
                (((StoredMacro) macros.get(i)).getStart_sequence() > 0)) {
                Array.set(array_list, 
                          ((StoredMacro)macros.get(i)).getStart_sequence(),
                          ((StoredMacro) macros.get(i)));
            }
        }

        // Now, execute in order
        for (int i = 0; i < Array.getLength(array_list); i ++) {
            StoredMacro m = (StoredMacro)Array.get(array_list, i);
            if (m != null) {
                MacroManager.execute(m);
            }
        }
    }
    
    /**
     * Execute the given macro.  The first time this is called
     * for a macro with a given name in the current runtime session,
     * the macro is compiled and the compiled code is stored before
     * executing.  Subsequent executions load in the precompiled
     * macro code.
     * 
     * @param m The Macro to execute
     */
    public static void execute(final StoredMacro m) {
        String name = m.getName();

        try {
		    org.python.core.PySystemState.initialize();
		
		    /* Try to pull a pre-compiled macro out of the hashtable.
		     * This is faster than having to re-compile the macro every
		     * time we want to execute it.
		     */
		    boolean compiled_available = true;
		    org.python.core.PyCode tmp_compiled = (org.python.core.PyCode)compiled_macros.get(name);
		    if (tmp_compiled == null) {
		        // compile the macro and save it
		        try {
		            tmp_compiled = org.python.core.__builtin__.compile(m.getCode(), m.getFilename(), "exec");
		            compiled_macros.put(name, tmp_compiled);
		        } catch (org.python.core.PyException e) {
		            JOptionPane.showInternalMessageDialog(
		                    DesktopManager.getDesktop(), 
		                    Locale.getString("MACRO_JYTHON_COMPILE_ERROR", m.getName(), e.toString()), 
		                    Locale.getString("ERROR_TITLE"), 
		                    JOptionPane.ERROR_MESSAGE);
		            compiled_available = false;
		        }
		    }
		    if (!compiled_available) {
		        return;
		    }
		
		    final org.python.core.PyCode compiled = tmp_compiled;
		    
		    // Set up the error and output handling streams
		    PipedOutputStream err_os = new PipedOutputStream();
		    PipedOutputStream out_os = new PipedOutputStream();
		
		    /* This class reads the STDOUT/STDERR from Jython so that it can
		     * be displayed in a lovely dialog when the macro is finished.
		     * We may want to extend this later to allow real-time redirecting
		     * of macro output */
		    class Reader extends Thread {
		        BufferedReader br;
		        String text = "";
		
		        Reader(BufferedReader br) { this.br = br; }
		        public String getText() { return text; }
		        public void run() {
		            try {
		                String line = br.readLine();
		                while (line != null) {
		                    text = text.concat(line);
		                    line = br.readLine();
		                    if (line != null) {
		                        text = text.concat(System.getProperty("line.separator"));
		                    }
		                }
		                br.close();
		            } catch (IOException e) {
		                System.err.println("MacroManager: Reader thread failed with exception: "+e.toString());
		            }
		        }
		    };
		    
		    Reader err_reader = null, out_reader = null;
		    try {
		        err_reader = new Reader(new BufferedReader(new InputStreamReader(new PipedInputStream(err_os))));
		        err_reader.start();
		    
		        out_reader = new Reader(new BufferedReader(new InputStreamReader(new PipedInputStream(out_os))));
		        out_reader.start();
		    } catch (IOException e) {
		        System.err.println("Got IOException starting up readers"+e.getMessage());
		    }
		
		    // Execute the macro
		    org.python.util.PythonInterpreter interp = new org.python.util.PythonInterpreter();
		    try { 
		        interp.setErr(err_os);
		        interp.setOut(out_os);
		        interp.exec(compiled);
		    } catch (org.python.core.PyException e) {
		        JOptionPane.showInternalMessageDialog(
		                DesktopManager.getDesktop(), 
		                Locale.getString("MACRO_JYTHON_EXCEPTION", m.getName(), e.toString()), 
		                Locale.getString("ERROR_TITLE"), 
		                JOptionPane.ERROR_MESSAGE);
		
		    }
		
		    // Clean up all the threads
		    try {
		        err_os.close();
		        out_os.close();
		        err_reader.join();
		        out_reader.join();
		    } catch (InterruptedException e) {
		        System.err.println("MacroManager: Main thread interrupted");
		    } catch (IOException e) {
		        System.err.println("MacroManager: IOException "+e.getMessage());
		    }
		
		    // Show the output of the macros
		    if (err_reader.getText().length() > 0) {
		        JOptionPane.showInternalMessageDialog(
		                DesktopManager.getDesktop(), 
		                Locale.getString("MACRO_OUTPUT_ERROR", m.getName(), 
		                err_reader.getText()), 
		                Locale.getString("ERROR_TITLE"), 
		                JOptionPane.ERROR_MESSAGE);
		    }
		
		    if (out_reader.getText().length() > 0) {
		        JOptionPane.showInternalMessageDialog(
		                DesktopManager.getDesktop(), 
		                Locale.getString("MACRO_INFORMATION_OUTPUT", m.getName(), 
		                        out_reader.getText()),
		                Locale.getString("MACRO_INFORMATION", m.getName()), 
		                JOptionPane.INFORMATION_MESSAGE);
		    }
		} catch (NoClassDefFoundError err) {
		    System.out.println("Jython ain't happenin, dude");
		}
    }
}
