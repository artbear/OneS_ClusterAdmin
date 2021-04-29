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

public class EditServerDialog extends Dialog {
	
	private Text txtRASHost;
	private Text txtRasPort;
	
	private Text txtManagerHost;
	private Text txtAgentPort;
	private Text txtManagerPort;
	
	private Group grpLocalRasParameters;
	private Button btnUseLocalRas;
	private Label lblV8Version;
	private Combo comboV8Versions;
	private Text txtLocalRasPort;
	private Text txtLocalRASLaunchString;
	private Label lblLocalRASLaunchString;
	
	private Button btnAutoconnect;
	private Button btnRebuild;
	private Label lblAgentUser;
	private Text txtAgentUser;
	private Label lblAgentPwd;
	private Text txtAgentPasswors;
	
	private Server serverParams;

	private String managerHost;
	private int managerPort;
	private int agentPort;
	private String rasHost;
	private int rasPort;
	private boolean useLocalRas;
	private String localRasV8version;
	private int localRasPort;
	private boolean autoconnect;
	private String agentUser;
	private String agentPasswors;

	/**
	 * Create the dialog.
	 * @param parentShell
	 * @param serverParams 
	 */
	public EditServerDialog(Shell parentShell, Server serverParams) {
		super(parentShell);
		setShellStyle(SWT.DIALOG_TRIM);
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
				extractServerParameters();
			}
		});
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.numColumns = 2;
//		new Label(container, SWT.NONE);
//		new Label(container, SWT.NONE);
		
		Group grpRasParameters = new Group(container, SWT.NONE);
		grpRasParameters.setText("RAS parameters");
		grpRasParameters.setLayout(new GridLayout(3, false));
		grpRasParameters.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		
		Label lblRASHost = new Label(grpRasParameters, SWT.NONE);
		lblRASHost.setText("Host");
		new Label(grpRasParameters, SWT.NONE);
		
		Label lblRasPort = new Label(grpRasParameters, SWT.NONE);
		lblRasPort.setSize(46, 15);
		lblRasPort.setText("Port");
		
		txtRASHost = new Text(grpRasParameters, SWT.BORDER);
		GridData gd_txtRASHost = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gd_txtRASHost.widthHint = 150;
		txtRASHost.setLayoutData(gd_txtRASHost);
		txtRASHost.setToolTipText("RAS host");
		
		txtRasPort = new Text(grpRasParameters, SWT.BORDER);
		txtRasPort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtRasPort.setToolTipText("RAS Port");
		
		Group grpRagentParameters = new Group(container, SWT.NONE);
		grpRagentParameters.setText("Cluster Agent Parameters");
		grpRagentParameters.setLayout(new GridLayout(3, false));
		grpRagentParameters.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		
		Label lblAgentHost = new Label(grpRagentParameters, SWT.NONE);
		lblAgentHost.setText("Host");
		new Label(grpRagentParameters, SWT.NONE);
		
		Label lblAgentPort = new Label(grpRagentParameters, SWT.NONE);
		lblAgentPort.setText("Port");
		
		txtManagerHost = new Text(grpRagentParameters, SWT.BORDER);
		GridData gd_txtAgentHost = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gd_txtAgentHost.widthHint = 150;
		txtManagerHost.setLayoutData(gd_txtAgentHost);
		txtManagerHost.setToolTipText("Agent host");
		
		txtAgentPort = new Text(grpRagentParameters, SWT.BORDER);
		txtAgentPort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtAgentPort.setToolTipText("Agent Port");
		new Label(grpRagentParameters, SWT.NONE);
		
		Label lblManagerPort = new Label(grpRagentParameters, SWT.NONE);
		lblManagerPort.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblManagerPort.setText("Cluster Manager Port:");
		
		txtManagerPort = new Text(grpRagentParameters, SWT.BORDER);
		txtManagerPort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtManagerPort.setToolTipText("manager port");
		
		lblAgentUser = new Label(grpRagentParameters, SWT.NONE);
		lblAgentUser.setText("User");
		new Label(grpRagentParameters, SWT.NONE);
		new Label(grpRagentParameters, SWT.NONE);
		
		txtAgentUser = new Text(grpRagentParameters, SWT.BORDER);
		txtAgentUser.setToolTipText("Agent host");
		GridData gd_txtAgentUser = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
		gd_txtAgentUser.widthHint = 150;
		txtAgentUser.setLayoutData(gd_txtAgentUser);
		new Label(grpRagentParameters, SWT.NONE);
		
		lblAgentPwd = new Label(grpRagentParameters, SWT.NONE);
		lblAgentPwd.setText("Password");
		new Label(grpRagentParameters, SWT.NONE);
		new Label(grpRagentParameters, SWT.NONE);
		
		txtAgentPasswors = new Text(grpRagentParameters, SWT.BORDER);
		txtAgentPasswors.setToolTipText("Agent password");
		GridData gd_txtAgentPwd = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
		gd_txtAgentPwd.widthHint = 150;
		txtAgentPasswors.setLayoutData(gd_txtAgentPwd);
		new Label(grpRagentParameters, SWT.NONE);
		
		grpLocalRasParameters = new Group(container, SWT.NONE);
		GridData gd_grpLocalRasParameters = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
		gd_grpLocalRasParameters.widthHint = 487;
		grpLocalRasParameters.setLayoutData(gd_grpLocalRasParameters);
		grpLocalRasParameters.setText("Local RAS parameters");
		grpLocalRasParameters.setLayout(new GridLayout(3, false));
		
		btnUseLocalRas = new Button(grpLocalRasParameters, SWT.CHECK);
		btnUseLocalRas.setText("Use local RAS");
		new Label(grpLocalRasParameters, SWT.NONE);
		new Label(grpLocalRasParameters, SWT.NONE);
		new Label(grpLocalRasParameters, SWT.NONE);
		
		lblV8Version = new Label(grpLocalRasParameters, SWT.NONE);
		lblV8Version.setText("RAS V8 version");
		
		comboV8Versions = new Combo(grpLocalRasParameters, SWT.NONE);
		GridData gd_comboV8Versions = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_comboV8Versions.widthHint = 150;
		comboV8Versions.setLayoutData(gd_comboV8Versions);
		// Заполнить список доступных платформ V8
		
		new Label(grpLocalRasParameters, SWT.NONE);
		
		Label lblLocalRasPort = new Label(grpLocalRasParameters, SWT.NONE);
		lblLocalRasPort.setSize(77, 15);
		lblLocalRasPort.setText("Local RAS port");
		
		txtLocalRasPort = new Text(grpLocalRasParameters, SWT.BORDER);
		txtLocalRasPort.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		txtLocalRasPort.setToolTipText("local RAS port");
		new Label(grpLocalRasParameters, SWT.NONE);
		
		lblLocalRASLaunchString = new Label(grpLocalRasParameters, SWT.NONE);
		lblLocalRASLaunchString.setSize(124, 15);
		lblLocalRASLaunchString.setText("Local RAS launch string:");
		
		btnRebuild = new Button(grpLocalRasParameters, SWT.NONE);
		btnRebuild.setSize(52, 25);
		btnRebuild.setText("Rebuild");
		new Label(grpLocalRasParameters, SWT.NONE);
		
		txtLocalRASLaunchString = new Text(grpLocalRasParameters, SWT.BORDER);
		GridData gd_txtLocalRASLaunchString = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gd_txtLocalRASLaunchString.widthHint = 307;
		txtLocalRASLaunchString.setLayoutData(gd_txtLocalRASLaunchString);
		txtLocalRASLaunchString.setSize(389, 21);
		txtLocalRASLaunchString.setToolTipText("LocalRASLaunchString");
		
		btnAutoconnect = new Button(container, SWT.CHECK);
		btnAutoconnect.setText("Automatic connection to the server at startup");
		new Label(container, SWT.NONE);

		initServerProperties();

		
		return container;
	}

	private void initServerProperties() {
		if (serverParams != null) {
			this.txtRASHost.setText(serverParams.rasHost);
			this.txtRasPort.setText(serverParams.getRasPortAsString());
			
			this.txtManagerHost.setText(serverParams.managerHost);
			this.txtAgentPort.setText(serverParams.getAgentPortAsString());
			this.txtManagerPort.setText(serverParams.getManagerPortAsString());
			
			this.btnUseLocalRas.setSelection(serverParams.useLocalRas);
			this.comboV8Versions.setText(serverParams.localRasV8version);
			this.txtLocalRasPort.setText(serverParams.getLocalRasPortAsString());
			this.btnAutoconnect.setSelection(serverParams.autoconnect);

			this.txtAgentUser.setText(serverParams.agentUser);
			this.txtAgentPasswors.setText(serverParams.agentPasswors);
		}
	}

	private void extractServerParameters() {
		managerHost 		= txtManagerHost.getText();
		managerPort 		= Integer.parseInt(txtManagerPort.getText());
		agentPort 			= Integer.parseInt(txtAgentPort.getText());
		rasHost 			= txtRASHost.getText();
		rasPort 			= Integer.parseInt(txtRasPort.getText());
		useLocalRas 		= btnUseLocalRas.getSelection();
		localRasPort 		= Integer.parseInt(txtLocalRasPort.getText());
		localRasV8version 	= comboV8Versions.getText();
		autoconnect 		= btnAutoconnect.getSelection();
		agentUser 			= txtAgentUser.getText();
		agentPasswors 		= txtAgentPasswors.getText();
	}

	private void saveNewServerProperties() {
		if (serverParams != null) {
			serverParams.setNewServerProperties(managerHost,
												managerPort,
												agentPort,
												rasHost,
												rasPort,
												useLocalRas,
												localRasPort,
												localRasV8version,
												autoconnect,
												agentUser,
												agentPasswors);
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
		return new Point(565, 387);
	}

}
