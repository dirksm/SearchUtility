package com.leewardassociates.search.threads;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.leewardassociates.search.constants.AppConstants;
import com.leewardassociates.search.models.ParamModel;

public class FileWriterThread implements Runnable {

	private static Logger log = LoggerFactory.getLogger(FileWriterThread.class);
    private ArrayBlockingQueue<String> buffer = null;
    private ParamModel params = null;
    private OutputStream target = null;
    private boolean closed = true;
    private static int counter = 0;
    private static int addcounter = 0;
    
    private FileWriterThread() {
        this.buffer = new ArrayBlockingQueue<String>(AppConstants.READER_QUEUE_SIZE);
    }

    public FileWriterThread(ParamModel params, OutputStream target) {
        this();
        this.params = params;
        this.target = target;
        this.closed = false;
    }
    
    
    public synchronized void addBuffer(String str) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("Adding " + str + " to buffer.  size is " + this.buffer.size());
        }
        if (!this.closed) {
            if (log.isDebugEnabled() && ++addcounter % 100 == 0) {
                log.debug("Added " + addcounter + " records to writer buffer.  buffer size is " + buffer.size());
            }
            try {
                this.buffer.offer(str, 365, TimeUnit.DAYS);
            } catch (InterruptedException e) {
                log.error("Exception adding to buffer: " + e.getMessage(), e);
            }
        } else {
            throw new IOException("Attempting to add to a buffer that has a closed target.");
        }
    }

    public boolean isComplete() {
        return this.buffer.isEmpty();
    }

    public void close() throws IOException {
        if (log.isInfoEnabled()) {
            log.info("Closing file writer thread...");
        }
        this.closed = true;
    }

    private void write(String str) throws IOException {
        target.write(str.getBytes());
        if (log.isDebugEnabled() && ++counter % 50 == 0) {
            log.debug("wrote " + counter + " xml records to file. Buffer size: " + buffer.size());
        }
    }
    
	@Override
	public void run() {
		if (log.isInfoEnabled()) {
            log.info("FileWriterThread started...");
        }
		try {
			String line = null;
			while (!this.closed) {
				if((line = this.buffer.poll()) != null) {
					this.write(line+"\n");
				}
			}
			log.info("Thread is closed. Finishing writing buffer to file.  Size: " + this.buffer.size());
            while ((line = this.buffer.poll()) != null) {
                this.write(line);
            }
		} catch (IOException ioe) {
			log.error("IOException running thread: " + ioe.getMessage(), ioe);
		}
	}

}
