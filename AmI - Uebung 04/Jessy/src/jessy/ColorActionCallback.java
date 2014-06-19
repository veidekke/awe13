package jessy;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.action.ActionArgumentValue;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;

public class ColorActionCallback extends ActionCallback {

	private String[] color;  
	
	protected ColorActionCallback(ActionInvocation actionInvocation) {
		super(actionInvocation);
	}
	  
    @Override
    public void success(ActionInvocation invocation) {
        ActionArgumentValue[] output = invocation.getOutput();
        setColor(output[0].getValue().toString().split(","));
    }

    @Override
    public void failure(ActionInvocation invocation,
                        UpnpResponse operation,
                        String defaultMsg) {
        System.err.println(defaultMsg);
    }

	public String[] getColor() {
		return color;
	}

	public void setColor(String[] color) {
		this.color = color;
	}
}
