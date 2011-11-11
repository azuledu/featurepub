package es.uva.idelab.featurepub.ThematicEncoder;

import java.io.OutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AbstractThematicEncoder {

	final Log logger = LogFactory.getLog(this.getClass());
	
	protected OutputStream outputStream;

	
	public OutputStream getOutputStream() {
		return outputStream;
	}

	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

}
