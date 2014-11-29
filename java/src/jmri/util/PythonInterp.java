// PythonInterp.java
package jmri.util;

import java.io.PipedReader;
import java.io.PipedWriter;
import javax.swing.JTextArea;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Support a single Jython interpreter for JMRI.
 * <P>
 * A standard JMRI-Jython dialog is defined by invoking the
 * "jython/jmri-defaults.py" file before starting the user code.
 * <P>
 * Access is via Java reflection so that both users and developers can work
 * without the jython.jar file in the classpath. To make it easier to read the
 * code, the "non-reflection" statements are in the comments.
 *
 * Note that there is Windows-specific handling of filenames in the execFile
 * routine. Since Java will occasionally treat the backslash character as a
 * character escape, we have to double it (to quote it) on Windows machines
 * where it might normally appear in a filename.
 *
 * @author	Bob Jacobsen Copyright (C) 2004
 * @version $Revision$
 */
public class PythonInterp {

    /**
     * Run a script file from it's filename.
     *
     * @param filename
     */
    static public void runScript(String filename) {
        execFile(filename);
    }

    static public void execFile(String filename) {
        // get a Python interpreter context, make sure it's ok
        getPythonInterpreter();
        if (interp == null) {
            log.error("Can't contine to execute command, could not create interpreter");
            return;
        }
        // if windows, need to process backslashes in filename
        if (SystemType.isWindows()) {
            filename = filename.replaceAll("\\\\", "\\\\\\\\");
        }

        interp.execfile(filename);
    }

    static public void execCommand(String command) {
        // get a Python interpreter context, make sure it's ok
        getPythonInterpreter();
        if (interp == null) {
            log.error("Can't contine to execute command, could not create interpreter");
            return;
        }

        interp.exec(command);
    }

    /**
     * All instance of this class share a single interpreter.
     */
    static private PythonInterpreter interp;

    /**
     * Provide an initialized Python interpreter.
     * <P>
     * If necessary to create one:
     * <UL>
     * <LI>Create the Python interpreter.
     * <LI>Read the default-setting file
     * </UL>
     * <P>
     * Interpreter is returned as an Object, which is to be invoked via
     * reflection.
     *
     * @return the Python interpreter for this session
     */
    synchronized static public Object getPythonInterpreter() {

        if (interp != null) {
            return interp;
        }

        // must create one.
        try {
            log.debug("create interpreter");
            PySystemState.initialize();

            interp = new PythonInterpreter();

            // have jython execute the default setup
            log.debug("load defaults from {}", defaultContextFile);
            execFile(FileUtil.getExternalFilename(defaultContextFile));

            return interp;

        } catch (Exception e) {
            log.error("Exception creating jython system objects", e);
            return null;
        }
    }

    /**
     * Provide access to the JTextArea containing the Jython VM output.
     * <P>
     * The output JTextArea is not created until this is invoked, so that code
     * that doesn't use this feature can run on GUI-less machines.
     *
     * @return component containing python output
     */
    static public JTextArea getOutputArea() {
        if (outputlog == null) {
            // convert to stored output

            try {
                // create the output area
                outputlog = new JTextArea();

                // Add the I/O pipes
                PipedWriter pw = new PipedWriter();

                interp.setErr(pw);
                interp.setOut(pw);

                // ensure the output pipe is read and stored into a
                // Swing TextArea data model
                PipedReader pr = new PipedReader(pw);
                PipeListener pl = new PipeListener(pr, outputlog);
                pl.start();
            } catch (Exception e) {
                log.error("Exception creating jython output area", e);
                return null;
            }
        }
        return outputlog;
    }

    /**
     * JTextArea containing the output
     */
    static private JTextArea outputlog = null;

    /**
     * Name of the file containing the Python code defining JMRI defaults
     */
    static String defaultContextFile = "program:jython/jmri_defaults.py";

    // initialize logging
    static Logger log = LoggerFactory.getLogger(PythonInterp.class.getName());

}

/* @(#)RunJythonScript.java */
