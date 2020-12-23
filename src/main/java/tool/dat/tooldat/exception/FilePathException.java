/**
 *
 */
package tool.dat.tooldat.exception;

/**
 * @author balabala1
 *
 */
public class FilePathException extends Exception {

	private static final String error = "File Path Error";

	public FilePathException(){
		super(error);
	}

	public FilePathException(String s){
		super(s);
	}

}
