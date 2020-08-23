package TreeWalker.CustomImportOrder;

import static java.io.File.createTempFile;
import static java.awt.Button.ABORT;
import static java.awt.print.Paper.*;
import static javax.swing.WindowConstants.*;

import java.awt.Button;  // violation without filter
import java.awt.Frame;  // violation without filter
import java.awt.Dialog;  // violation without filter
import java.awt.color.ColorSpace;  // violation without filter
import java.awt.event.ActionEvent;  // violation without filter
import javax.swing.JComponent;  // violation without filter
import javax.swing.JTable;  // violation without filter
import java.io.File;  // violation without filter
import java.io.IOException;  // violation without filter
import java.io.InputStream;  // violation without filter
import java.io.Reader;  // violation without filter

import com.puppycrawl.tools.checkstyle.*;

public class Test {
}
