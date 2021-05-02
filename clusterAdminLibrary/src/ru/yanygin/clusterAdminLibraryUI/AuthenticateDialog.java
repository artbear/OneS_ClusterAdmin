package ru.yanygin.clusterAdminLibraryUI;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com._1c.v8.ibis.admin.IClusterInfo;
import ru.yanygin.clusterAdminLibrary.ClusterConnector_delete;
import ru.yanygin.clusterAdminLibrary.Config.Server;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.DisposeEvent;

public class AuthenticateDialog extends Dialog {
	
	private Text txtUsername;
	private Text txtPassword;
	private Label lblAuthenticateInfo;
	private Label lblAuthExcpMessage;
	
	private IClusterInfo clusterInfo;
//	private ClusterConnector clusterConnector;
	private Server server;

	private String username;
	private String password;
	private String authExcpMessage;
	private String authenticateType;
	
	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	/**
	 * Create the dialog.
	 * @param parentShell
	 * @param serverParams 
	 */
	public AuthenticateDialog(Shell parentShell, Server server, IClusterInfo clusterInfo, String username, String authenticateType, String authExcpMessage) {
		super(parentShell);
		setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

//		super.configureShell(parentShell);
//		parentShell.setText("Parameters of the 1C:Enterprise infobase");
	    
		this.clusterInfo 		= clusterInfo;
		this.server 			= server;
		this.username 			= username;
		this.authExcpMessage 	= authExcpMessage;
		this.authenticateType 	= authenticateType;
		
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		parent.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
//				extractInfobaseVariablesFromControls();
			}
		});
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.numColumns = 2;
		
		lblAuthenticateInfo = new Label(container, SWT.NONE);
		lblAuthenticateInfo.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 2, 1));
		lblAuthenticateInfo.setText(authenticateType);
		
		Label lblUsername = new Label(container, SWT.NONE);
		lblUsername.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblUsername.setText("Username");
		
		txtUsername = new Text(container, SWT.BORDER);
		txtUsername.setToolTipText("Username");
		txtUsername.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblPassword = new Label(container, SWT.NONE);
		lblPassword.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPassword.setText("Password");
		
		txtPassword = new Text(container, SWT.BORDER);
		txtPassword.setToolTipText("Password");
		txtPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		lblAuthExcpMessage = new Label(container, SWT.NONE);
		lblAuthExcpMessage.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
		lblAuthExcpMessage.setText(authExcpMessage);

		initServerProperties();

		
		return container;
	}

	private void initServerProperties() {
		this.txtUsername.setText(getUsername());
		
//		if (infoBaseInfo != null) {
//			
//			// Common properties
//			this.txtInfobaseName.setText(infoBaseInfo.getName());
//			this.txtInfobaseDescription.setText(infoBaseInfo.getDescr());
//			this.txtSecurityLevel.setText(Integer.toString(infoBaseInfo.getSecurityLevel()));
//			this.btnAllowDistributeLicense.setSelection(infoBaseInfo.getLicenseDistributionAllowed() == 1);
//			this.btnSheduledJobsDenied.setSelection(infoBaseInfo.isScheduledJobsDenied());
//			
//			// DB properties
//			this.txtServerDBName.setText(infoBaseInfo.getDbServerName());
//			this.comboServerDBType.setText(infoBaseInfo.getDbms());
//			this.txtDatabaseDbName.setText(infoBaseInfo.getDbName());
//			this.txtDatabaseDbUser.setText(infoBaseInfo.getDbUser());
//			this.txtDatabaseDbPassword.setText(infoBaseInfo.getDbPassword());
//			
//			// Lock properties
//			this.comboLocale.setText(infoBaseInfo.getLocale());
//						
//			this.comboDateOffset.setText(Integer.toString(infoBaseInfo.getDateOffset()));
//			this.btnInfobaseCreationMode.setSelection(false);
//			
//			
//		}
	}

	private void saveNewServerProperties() {

		try {

			server.authenticateAgent(getUsername(), getPassword());

			server.authenticateCluster(clusterInfo.getClusterId(), getUsername(), getPassword());

			server.addInfoBaseCredentials(clusterInfo.getClusterId(), getUsername(), getPassword());

		} catch (Exception excp) {
			excp.printStackTrace();
			MessageBox messageBox = new MessageBox(getParentShell());
			messageBox.setMessage(excp.getLocalizedMessage());
			messageBox.open();
		}

	}

	private void extractInfobaseVariablesFromControls() {
		
		username = txtUsername.getText();
		password = txtPassword.getText();
		
	}
	
	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button button = createButton(parent, IDialogConstants.FINISH_ID, IDialogConstants.OK_LABEL, true);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				extractInfobaseVariablesFromControls();
				saveNewServerProperties();
				close();
			}
		});
		
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(408, 220);
	}

}
