/**
 *
 */
package tool.dat.tooldat;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author balabala1
 *
 */
@Scope("prototype")
@Component
public class Points {

	public enum Status{
		IMPLEMENTED, NOT_IMPLEMENTED, NOT_APPLICABLE, Check, NON
	}

	Status status = Status.NOT_APPLICABLE;

	String remarks = "";

	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Status status) {
		this.status =  status;
	}

	/**
	 * @return the remarks
	 */
	public String getRemarks() {
		return remarks;
	}

	/**
	 * @param remarks the remarks to set
	 */
	public void setRemarks(String remarks) {
		this.remarks = this.remarks + remarks + "\n\r";
	}

}
