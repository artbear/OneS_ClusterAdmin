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

public class EditServerDialog extends Dialog {
	
	private Button btnAutoconnect;
	private Text txtServerHost;
	private Text txtManagerPort;
	private Text txtRemoteRasPort;
	
	private Button btnUseLocalRas;
	private Text txtLocalRasPort;
	private Combo comboV8Versions;
	
	private Server serverParams;

	private String serverHost;
	private int managerPort;
	private int remoteRasPort;
	private boolean useLocalRas;
	private String localRasV8version;
	private int localRasPort;
	private boolean autoconnect;

	/**
	 * Create the dialog.
	 * @param parentShell
	 * @param serverParams 
	 */
	public EditServerDialog(Shell parentShell, Server serverParams) {
		super(parentShell);
		parentShell.setText("Parameters of the central server 1C:Enterprise");

		this.serverParams = serverParams;
	}

//	public EditServerConnectionDialog(Shell parentShell, clusterProvider) {
//		super(parentShell);
//		this.serverParams = new Server("newServerAddress:1541");
//	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		parent.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				serverHost 			= txtServerHost.getText();
				managerPort 		= Integer.parseInt(txtManagerPort.getText());
				remoteRasPort 		= Integer.parseInt(txtRemoteRasPort.getText());
				useLocalRas 		= btnUseLocalRas.getSelection();
				localRasV8version 	= comboV8Versions.getText();
				localRasPort 		= Integer.parseInt(txtLocalRasPort.getText());
				autoconnect 		= btnAutoconnect.getSelection();
			}
		});
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.numColumns = 2;
		
		btnAutoconnect = new Button(container, SWT.CHECK);
		btnAutoconnect.setText("Autoconnect");
		new Label(container, SWT.NONE);
		
		Label lblServerAddress = new Label(container, SWT.NONE);
		lblServerAddress.setText("Server Address");
		
		txtServerHost = new Text(container, SWT.BORDER);
		txtServerHost.setToolTipText("server address");
		txtServerHost.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblManagerPort = new Label(container, SWT.NONE);
		lblManagerPort.setText("Manager Port");
		
		txtManagerPort = new Text(container, SWT.BORDER);
		txtManagerPort.setTouchEnabled(true);
		txtManagerPort.setToolTipText("agent port");
		txtManagerPort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblRemoteRasPort = new Label(container, SWT.NONE);
		lblRemoteRasPort.setText("Remote RAS Port");
		
		txtRemoteRasPort = new Text(container, SWT.BORDER);
		txtRemoteRasPort.setToolTipText("RAS Port");
		txtRemoteRasPort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnUseLocalRas = new Button(container, SWT.CHECK);
		btnUseLocalRas.setText("Use local RAS");
		
		comboV8Versions = new Combo(container, SWT.NONE);
		comboV8Versions.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblLocalRasPort = new Label(container, SWT.NONE);
		lblLocalRasPort.setText("Local RAS port");
		
		txtLocalRasPort = new Text(container, SWT.BORDER);
		txtLocalRasPort.setToolTipText("local RAS port");
		txtLocalRasPort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		initServerProperties();

		
		return container;
	}

	private void initServerProperties() {
		if (serverParams != null) {
			this.txtServerHost.setText(serverParams.serverHost);
			this.txtManagerPort.setText(serverParams.getManagerPortAsString());
			this.txtRemoteRasPort.setText(serverParams.getRemoteRasPortAsString());
			this.btnUseLocalRas.setSelection(serverParams.useLocalRas);
			this.comboV8Versions.setText(serverParams.localRasV8version);
			this.txtLocalRasPort.setText(serverParams.getLocalRasPortAsString());
			this.btnAutoconnect.setSelection(serverParams.autoconnect);
		}
	}

	private void saveNewServerProperties() {
		if (serverParams != null) {
			serverParams.setNewServerProperties(
//												this.txtServerAddress.getText(),
//												Integer.parseInt(this.txtManagerPort.getText()),
//												Integer.parseInt(this.txtRemoteRasPort.getText()),
//												this.btnUseLocalRas.getSelection(),
//												Integer.parseInt(this.txtLocalRasPort.getText()),
//												this.comboV8Versions.getText(),
//												this.btnAutoconnect.getSelection());
			
												serverHost,
												managerPort,
												remoteRasPort,
												useLocalRas,
												localRasPort,
												localRasV8version,
												autoconnect);
			
//			serverConfig.serverAddress = this.txtServerAddress.getText();
//			serverConfig.managerPort = this.txtManagerPort.getText();
//			serverConfig.remoteRasPort = this.txtRemoteRasPort.getText();
//			serverConfig.localRasPort = this.txtLocalRasPort.getText();
//			serverConfig.autoconnect = this.btnAutoconnect.getSelection();
//			serverConfig.useLocalRas = this.btnUseLocalRas.getSelection();
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
		return new Point(450, 300);
	}

}