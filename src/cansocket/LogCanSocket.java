package cansocket;

import java.io.*;

public class LogCanSocket implements CanSocket
{
	CanSocket base;
	Writer log;

	public LogCanSocket(CanSocket base, String logfile) throws IOException
	{
		this.base = base;
		log = new FileWriter(logfile, /* append */ true);
	}

	public CanMessage read() throws IOException
	{
		return log(base.read());
	}

	public void write(CanMessage msg) throws IOException
	{
		base.write(log(msg));
	}

	/*** phony: keep interface definition happy ***/
	public CanMessage recv() 
	{
	    return( new CanMessage( (short)98, 100, new byte[CanMessage.MSG_BODY] ));
	}

	public void send( CanMessage msg )
	{}
	/*** end phony ***/

	protected CanMessage log(CanMessage msg) throws IOException
	{
		StringBuffer buf = new StringBuffer();
		byte body[] = msg.getBody();
		buf.append(msg.getId())
			.append(' ').append(msg.getTimestamp())
			.append(' ').append(body.length);
		for(int i = 0; i < body.length; ++i)
			buf.append(' ').append(body[i]);
		buf.append('\n');
		log.write(buf.toString());
		log.flush();
		return msg;
	}

	public void close() throws IOException
	{
		base.close();
		log.close();
	}

	public void flush() throws IOException
	{
		base.flush();
	}
}
