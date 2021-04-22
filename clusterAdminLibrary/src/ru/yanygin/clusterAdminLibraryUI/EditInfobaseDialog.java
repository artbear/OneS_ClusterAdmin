package ru.yanygin.clusterAdminLibraryUI;

import java.util.Date;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com._1c.v8.ibis.admin.InfoBaseInfo;

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
	private Button btnSheduledJobsLock;
	private Button btnAllowDistributeLicense;
	private Button btnMandatoryUseExternalManagement;
	private Text txtInfobaseName;
	private Text txtSecureConnection;
	private Text txtServerDBName;
	private Text txtDatabaseName;
	private Text txtPresent;
	private Text txtDatabaseLogin;
	private Text txtPermissionCode;
	private Text txtLockParameter;
	private Text txtExternalSessionManagement;
	private Text txtSecurityProfile;
	private Text txtSafeModeSecurityProfile;
	private Text txtLockMessage;
	private Combo comboServerDBType;
	private DateTime dateTimeLockStart;
	private DateTime dateTimeLockStartTime;
	private DateTime dateTimeLockStop;
	private DateTime dateTimeLockStopTime;
	private Text txtDatabasePwd;
	
	private InfoBaseInfo infoBaseInfo;

	private String infobaseName;
	private String infobasePresent;
	private String secureConnection;
	
	private String serverDBName;
	private String serverDBType; // MSSQLServer, PostgreSQL
	private String databaseName;
	private String databaseLogin;
	private String databasePwd;
	
	private boolean allowDistributeLicense;
	
	private boolean sessionsLock;
	private String lockStartDateTime;
//	private String lockStartTime;
	private String lockStopDateTime;
//	private String lockStopTime;
	private String lockMessage;
	private String permissionCode;
	private String lockParameter;
	
	private boolean sheduledJobsLock;
	
	private String externalSessionManagement;
	private boolean mandatoryUseExternalManagement;
	
	private String securityProfile;
	private String safeModeSecurityProfile;
	

	/**
	 * Create the dialog.
	 * @param parentShell
	 * @param serverParams 
	 */
	public EditInfobaseDialog(Shell parentShell, InfoBaseInfo infoBaseInfo) {
		super(parentShell);
		parentShell.setText("Parameters of the 1C:Enterprise infobase");

		this.infoBaseInfo = infoBaseInfo;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		parent.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				infobaseName 		= txtInfobaseName.getText();
				infobasePresent 	= txtPresent.getText();
				secureConnection 	= txtSecureConnection.getText();
				
				serverDBName 	= txtServerDBName.getText();
				serverDBType 	= comboServerDBType.getText();
				databaseName 	= txtDatabaseName.getText();
				databaseLogin 	= txtDatabaseLogin.getText();
				databasePwd 	= txtDatabasePwd.getText();
				
				allowDistributeLicense 	= btnAllowDistributeLicense.getSelection();
				
				sessionsLock 	= btnSessionsLock.getSelection();
				lockStartDateTime 	= convertDateTime(dateTimeLockStart, dateTimeLockStartTime);
//				lockStartTime 	= dateTimeLockStartTime.getText();
				lockStopDateTime 	= convertDateTime(dateTimeLockStop, dateTimeLockStopTime);
//				lockStopTime 	= dateTimeLockStopTime.getText();
				lockMessage 	= txtLockMessage.getText();
				permissionCode 	= txtPermissionCode.getText();
				lockParameter 	= txtLockParameter.getText();
				
				sheduledJobsLock = btnSheduledJobsLock.getSelection();
				
				externalSessionManagement 		= txtExternalSessionManagement.getText();
				mandatoryUseExternalManagement 	= btnMandatoryUseExternalManagement.getSelection();
				
				securityProfile 		= txtSecurityProfile.getText();
				safeModeSecurityProfile = txtSafeModeSecurityProfile.getText();

			}

			private String convertDateTime(DateTime date, DateTime time) {
				String dateTimeString = "";
				
				int year = date.getYear();
				
				return Integer.toString(year);
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
		
		Label lblPresent = new Label(container, SWT.NONE);
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
		
		Label lblServerDBType = new Label(container, SWT.NONE);
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
		
		Label lblDatabaseLogin = new Label(container, SWT.NONE);
		lblDatabaseLogin.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDatabaseLogin.setText("Database login");
		
		txtDatabaseLogin = new Text(container, SWT.BORDER);
		txtDatabaseLogin.setToolTipText("Database login");
		txtDatabaseLogin.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblDatabasePwd = new Label(container, SWT.NONE);
		lblDatabasePwd.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDatabasePwd.setAlignment(SWT.RIGHT);
		lblDatabasePwd.setText("Database password");
		
		txtDatabasePwd = new Text(container, SWT.BORDER | SWT.PASSWORD);
		txtDatabasePwd.setToolTipText("Database password");
		txtDatabasePwd.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);
		
		btnAllowDistributeLicense = new Button(container, SWT.CHECK);
		btnAllowDistributeLicense.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnAllowDistributeLicense.setText("Allow distribute license at 1C:Enterprise server");
		new Label(container, SWT.NONE);
		
		btnSessionsLock = new Button(container, SWT.CHECK);
		btnSessionsLock.setText("Sessions lock");
		
		Label lblSessionsLockStart = new Label(container, SWT.NONE);
		lblSessionsLockStart.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSessionsLockStart.setText("Lock start:");
		
		Composite compositeTimeLockStart = new Composite(container, SWT.NONE);
		compositeTimeLockStart.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		dateTimeLockStart = new DateTime(compositeTimeLockStart, SWT.BORDER | SWT.DATE | SWT.DROP_DOWN);
		
		dateTimeLockStartTime = new DateTime(compositeTimeLockStart, SWT.BORDER | SWT.TIME);
		
		Label lblSessionsLockStop = new Label(container, SWT.NONE);
		lblSessionsLockStop.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSessionsLockStop.setText("Lock stop:");
		
		Composite compositeTimeLockStop = new Composite(container, SWT.NONE);
		compositeTimeLockStop.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		dateTimeLockStop = new DateTime(compositeTimeLockStop, SWT.NONE | SWT.DROP_DOWN);
		
		dateTimeLockStopTime = new DateTime(compositeTimeLockStop, SWT.BORDER | SWT.TIME);
//		new Label(container, SWT.NONE);
//		new Label(container, SWT.NONE);

		Label lblLockMessage = new Label(container, SWT.NONE);
		lblLockMessage.setText("Lock message:");
		
		txtLockMessage = new Text(container, SWT.BORDER);
		txtLockMessage.setToolTipText("Lock message");
		GridData gd_txtLockMessage = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		gd_txtLockMessage.heightHint = 63;
		txtLockMessage.setLayoutData(gd_txtLockMessage);
		
		Label lblPermissionCode = new Label(container, SWT.NONE);
		lblPermissionCode.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPermissionCode.setText("Permission code:");
		
		txtPermissionCode = new Text(container, SWT.BORDER);
		txtPermissionCode.setToolTipText("Permission code");
		txtPermissionCode.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblLockParameter = new Label(container, SWT.NONE);
		lblLockParameter.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblLockParameter.setText("Lock parameter");
		
		txtLockParameter = new Text(container, SWT.BORDER);
		txtLockParameter.setToolTipText("Lock parameter");
		txtLockParameter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);
		
		btnSheduledJobsLock = new Button(container, SWT.CHECK);
		btnSheduledJobsLock.setText("Sheduled jobs lock");
		
		Label lblExternalSessionManagement = new Label(container, SWT.NONE);
		lblExternalSessionManagement.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblExternalSessionManagement.setText("External session management");
		
		txtExternalSessionManagement = new Text(container, SWT.BORDER);
		txtExternalSessionManagement.setToolTipText("External session management");
		txtExternalSessionManagement.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);
		
		btnMandatoryUseExternalManagement = new Button(container, SWT.CHECK);
		btnMandatoryUseExternalManagement.setText("Mandatory use of external management");
		
		Label lblSecurityProfile = new Label(container, SWT.NONE);
		lblSecurityProfile.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSecurityProfile.setText("Security profile");
		
		txtSecurityProfile = new Text(container, SWT.BORDER);
		txtSecurityProfile.setToolTipText("Security profile");
		txtSecurityProfile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblSafeModeSecurity = new Label(container, SWT.NONE);
		lblSafeModeSecurity.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSafeModeSecurity.setText("Safe mode security profile");
		
		txtSafeModeSecurityProfile = new Text(container, SWT.BORDER);
		txtSafeModeSecurityProfile.setToolTipText("Safe mode security profile");
		txtSafeModeSecurityProfile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		initServerProperties();

		
		return container;
	}

	private void initServerProperties() {
		if (infoBaseInfo != null) {
			this.txtInfobaseName.setText(infoBaseInfo.getName());
			this.txtPresent.setText(infoBaseInfo.getDescr());
			this.txtSecureConnection.setText(Integer.toString(infoBaseInfo.getSecurityLevel()));
			
			this.txtServerDBName.setText(infoBaseInfo.getDbServerName());
			this.comboServerDBType.setText(infoBaseInfo.getDbms());
			this.txtDatabaseName.setText(infoBaseInfo.getDbName());
			this.txtDatabaseLogin.setText(infoBaseInfo.getDbUser());
			this.txtDatabasePwd.setText(infoBaseInfo.getDbPassword());
			
			this.btnSessionsLock.setSelection(infoBaseInfo.isSessionsDenied());
			
			Date deniedFrom = infoBaseInfo.getDeniedFrom();
			Date deniedTo  	= infoBaseInfo.getDeniedTo();
//			this.dateTimeLockStart.setDate(deniedFrom.getYear());
//			this.dateTimeLockStartTime 	= dateTimeLockStartTime.getText();
//			this.dateTimeLockStop  		= lockStopDateTime;
//			this.dateTimeLockStopTime  	= lockStopTime;
			this.txtLockMessage.setText(infoBaseInfo.getDeniedMessage());
			this.txtPermissionCode.setText(infoBaseInfo.getPermissionCode());
			this.txtLockParameter.setText(infoBaseInfo.getDeniedParameter());
			
			this.btnAllowDistributeLicense.setSelection(infoBaseInfo.isScheduledJobsDenied());
			
			this.txtExternalSessionManagement.setText(infoBaseInfo.getExternalSessionManagerConnectionString());
			this.btnMandatoryUseExternalManagement.setSelection(infoBaseInfo.getExternalSessionManagerRequired());
			
			this.txtSecurityProfile.setText(infoBaseInfo.getSecurityProfileName());
			this.txtSafeModeSecurityProfile.setText(infoBaseInfo.getSafeModeSecurityProfileName());
			
			
		}
	}

	private void saveNewServerProperties() {
//		if (serverParams != null) {
//			serverParams.setNewServerProperties(			
//												infobaseName,
//												secureConnection,
//												remoteRasPort,
//												useLocalRas,
//												localRasPort,
//												localRasV8version,
//												autoconnect);
//			
//		}
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
