package es.uva.idelab.featurepub.encoder;

import java.io.OutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AbstractEncoder {

	final Log logger = LogFactory.getLog(this.getClass());
	
	protected OutputStream outputStream;

	
	public OutputStream getOutputStream() {
		return outputStream;
	}

	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

}
