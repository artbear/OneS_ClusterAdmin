package ru.yanygin.clusterAdminLibraryUI;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ru.yanygin.clusterAdminLibrary.Config.Server;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.DateTime;

public class EditInfobaseDialog extends Dialog {
	
	private Button btnSessionsLock;
	private Text txtInfobaseName;
	private Text txtSecureConnection;
	private Text txtServerDBName;
	
	private Button btnSheduledJobsLock;
	private Text txtDatabaseName;
	private Combo comboServerDBType;
	
	private Server serverParams;

	private String serverHost;
	private int managerPort;
	private int remoteRasPort;
	private boolean useLocalRas;
	private String localRasV8version;
	private int localRasPort;
	private boolean autoconnect;
	private Label lblPresent;
	private Text txtPresent;
	private Label lblServerDBType;
	private Label lblDatabaseLogin;
	private Text txtDatabaseLogin;
	private Button btnAllowDistributeLicense;
	private Label lblSessionsLockStart;
	private Label lblSessionsLockStop;
	private Label lblPermissionCode;
	private Text txtPermissionCode;
	private Label lblLockParameter;
	private Text txtLockParameter;
	private Label lblExternalSessionManagement;
	private Label lblSecurityProfile;
	private Label lblSafeModeSecurity;
	private Text txtExternalSessionManagement;
	private Text txtSecurityProfile;
	private Text txtSafeModeSecurityProfile;
	private Button btnMandatoryUseExternalManagement;

	/**
	 * Create the dialog.
	 * @param parentShell
	 * @param serverParams 
	 */
	public EditInfobaseDialog(Shell parentShell, Server serverParams) {
		super(parentShell);
		parentShell.setText("Parameters of the central server 1C:Enterprise");

		this.serverParams = serverParams;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		parent.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				serverHost 			= txtInfobaseName.getText();
				managerPort 		= Integer.parseInt(txtSecureConnection.getText());
				remoteRasPort 		= Integer.parseInt(txtServerDBName.getText());
				useLocalRas 		= btnSheduledJobsLock.getSelection();
				localRasV8version 	= comboServerDBType.getText();
				localRasPort 		= Integer.parseInt(txtDatabaseName.getText());
				autoconnect 		= btnSessionsLock.getSelection();
			}
		});
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.numColumns = 2;
		
		Label lblInfobaseName = new Label(container, SWT.NONE);
		lblInfobaseName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblInfobaseName.setText("Infobase name");
		
		txtInfobaseName = new Text(container, SWT.BORDER);
		txtInfobaseName.setToolTipText("Infobase name");
		txtInfobaseName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		lblPresent = new Label(container, SWT.NONE);
		lblPresent.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
//		lblPresent.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPresent.setText("Present");
		
		txtPresent = new Text(container, SWT.BORDER);
		txtPresent.setToolTipText("Present");
		txtPresent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblSecureConnection = new Label(container, SWT.NONE);
		lblSecureConnection.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSecureConnection.setToolTipText("");
		lblSecureConnection.setText("Secure connection");
		
		txtSecureConnection = new Text(container, SWT.BORDER);
		txtSecureConnection.setTouchEnabled(true);
		txtSecureConnection.setToolTipText("Secure connection");
		txtSecureConnection.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblServerDBName = new Label(container, SWT.NONE);
		lblServerDBName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblServerDBName.setText("Server DB name");
		
		txtServerDBName = new Text(container, SWT.BORDER);
		txtServerDBName.setToolTipText("Server DB name");
		txtServerDBName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		lblServerDBType = new Label(container, SWT.NONE);
		lblServerDBType.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblServerDBType.setText("Server DB type");
		
		comboServerDBType = new Combo(container, SWT.NONE);
		comboServerDBType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblDatabaseName = new Label(container, SWT.NONE);
		lblDatabaseName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDatabaseName.setText("Database name");
		
		txtDatabaseName = new Text(container, SWT.BORDER);
		txtDatabaseName.setToolTipText("Database name");
		txtDatabaseName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		lblDatabaseLogin = new Label(container, SWT.NONE);
		lblDatabaseLogin.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDatabaseLogin.setText("Database login");
		
		txtDatabaseLogin = new Text(container, SWT.BORDER);
		txtDatabaseLogin.setToolTipText("Database login");
		txtDatabaseLogin.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblDatabasePwd = new Label(container, SWT.NONE);
		lblDatabasePwd.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDatabasePwd.setAlignment(SWT.RIGHT);
		lblDatabasePwd.setText("Database password");
		
		Text txtDatabasePwd = new Text(container, SWT.BORDER | SWT.PASSWORD);
		txtDatabasePwd.setToolTipText("Database password");
		txtDatabasePwd.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);
		
		btnAllowDistributeLicense = new Button(container, SWT.CHECK);
		btnAllowDistributeLicense.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnAllowDistributeLicense.setText("Allow distribute license at 1C:Enterprise server");
		
		btnSessionsLock = new Button(container, SWT.CHECK);
		btnSessionsLock.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnSessionsLock.setText("Sessions lock");
		
		lblSessionsLockStart = new Label(container, SWT.NONE);
		lblSessionsLockStart.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblSessionsLockStart.setText("Lock start");
		
		lblSessionsLockStop = new Label(container, SWT.NONE);
		lblSessionsLockStop.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblSessionsLockStop.setText("Lock stop");
		
		DateTime dateTimeLockStart = new DateTime(container, SWT.BORDER | SWT.DATE | SWT.DROP_DOWN);
		dateTimeLockStart.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		
		DateTime dateTimeLockStop = new DateTime(container, SWT.NONE | SWT.DROP_DOWN);
		dateTimeLockStop.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		
		DateTime dateTimeLockStartTime = new DateTime(container, SWT.BORDER | SWT.TIME | SWT.DROP_DOWN);
		dateTimeLockStartTime.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		new Label(container, SWT.NONE);
		
		Label lblLockMessage = new Label(container, SWT.NONE);
		lblLockMessage.setText("Lock message:");
		
		Text txtAllowDistributeLicense = new Text(container, SWT.BORDER);
		txtAllowDistributeLicense.setToolTipText("Database password");
		GridData gd_txtAllowDistributeLicense = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		gd_txtAllowDistributeLicense.heightHint = 63;
		txtAllowDistributeLicense.setLayoutData(gd_txtAllowDistributeLicense);
		
		lblPermissionCode = new Label(container, SWT.NONE);
		lblPermissionCode.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPermissionCode.setText("Permission code:");
		
		txtPermissionCode = new Text(container, SWT.BORDER);
		txtPermissionCode.setToolTipText("Permission code");
		txtPermissionCode.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		lblLockParameter = new Label(container, SWT.NONE);
		lblLockParameter.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblLockParameter.setText("Lock parameter");
		
		txtLockParameter = new Text(container, SWT.BORDER);
		txtLockParameter.setToolTipText("Lock parameter");
		txtLockParameter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);
		
		btnSheduledJobsLock = new Button(container, SWT.CHECK);
		btnSheduledJobsLock.setText("Sheduled jobs lock");
		
		lblExternalSessionManagement = new Label(container, SWT.NONE);
		lblExternalSessionManagement.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblExternalSessionManagement.setText("External session management");
		
		txtExternalSessionManagement = new Text(container, SWT.BORDER);
		txtExternalSessionManagement.setToolTipText("External session management");
		txtExternalSessionManagement.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);
		
		btnMandatoryUseExternalManagement = new Button(container, SWT.CHECK);
		btnMandatoryUseExternalManagement.setText("Mandatory use of external management");
		
		lblSecurityProfile = new Label(container, SWT.NONE);
		lblSecurityProfile.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSecurityProfile.setText("Security profile");
		
		txtSecurityProfile = new Text(container, SWT.BORDER);
		txtSecurityProfile.setToolTipText("Security profile");
		txtSecurityProfile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		lblSafeModeSecurity = new Label(container, SWT.NONE);
		lblSafeModeSecurity.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSafeModeSecurity.setText("Safe mode security profile");
		
		txtSafeModeSecurityProfile = new Text(container, SWT.BORDER);
		txtSafeModeSecurityProfile.setToolTipText("Safe mode security profile");
		txtSafeModeSecurityProfile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		initServerProperties();

		
		return container;
	}

	private void initServerProperties() {
		if (serverParams != null) {
			this.txtInfobaseName.setText(serverParams.serverHost);
			this.txtSecureConnection.setText(serverParams.getManagerPortAsString());
			this.txtServerDBName.setText(serverParams.getRemoteRasPortAsString());
			this.btnSheduledJobsLock.setSelection(serverParams.useLocalRas);
			this.comboServerDBType.setText(serverParams.localRasV8version);
			this.txtDatabaseName.setText(serverParams.getLocalRasPortAsString());
			this.btnSessionsLock.setSelection(serverParams.autoconnect);
		}
	}

	private void saveNewServerProperties() {
		if (serverParams != null) {
			serverParams.setNewServerProperties(			
												serverHost,
												managerPort,
												remoteRasPort,
												useLocalRas,
												localRasPort,
												localRasV8version,
												autoconnect);
			
		}
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button button = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveNewServerProperties();
			}
		});
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 740);
	}

}
