package org.latexlab.docs.editor.advanced.client;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import org.latexlab.clsi.client.async.CompileCallback;
import org.latexlab.clsi.client.async.ReadyCallback;
import org.latexlab.clsi.client.local.ClsiLocalService;
import org.latexlab.clsi.client.protocol.ClsiResourceReference;
import org.latexlab.clsi.client.protocol.ClsiServiceCompileResponse;
import org.latexlab.clsi.client.remote.ClsiRemoteService;
import org.latexlab.docs.client.DocsEditorSettings;
import org.latexlab.docs.client.LatexLabService;
import org.latexlab.docs.client.LatexLabServiceAsync;
import org.latexlab.docs.client.commands.Command;
import org.latexlab.docs.client.commands.CurrentDocumentCloseCommand;
import org.latexlab.docs.client.commands.CurrentDocumentCompileCommand;
import org.latexlab.docs.client.commands.CurrentDocumentCompileLocalCommand;
import org.latexlab.docs.client.commands.CurrentDocumentCopyCommand;
import org.latexlab.docs.client.commands.CurrentDocumentDeleteCommand;
import org.latexlab.docs.client.commands.CurrentDocumentExportCommand;
import org.latexlab.docs.client.commands.CurrentDocumentLoadContentsCommand;
import org.latexlab.docs.client.commands.CurrentDocumentRenameCommand;
import org.latexlab.docs.client.commands.CurrentDocumentRevisionHistoryCommand;
import org.latexlab.docs.client.commands.CurrentDocumentSaveAndCloseCommand;
import org.latexlab.docs.client.commands.CurrentDocumentSaveCommand;
import org.latexlab.docs.client.commands.ExistingDocumentLoadCommand;
import org.latexlab.docs.client.commands.ExistingDocumentOpenCommand;
import org.latexlab.docs.client.commands.FileDialogListDocumentsCommand;
import org.latexlab.docs.client.commands.FileDialogOpenDocumentCommand;
import org.latexlab.docs.client.commands.FileDialogStarDocumentCommand;
import org.latexlab.docs.client.commands.FileDialogUnstarDocumentCommand;
import org.latexlab.docs.client.commands.NewDocumentLoadCommand;
import org.latexlab.docs.client.commands.NewDocumentStartCommand;
import org.latexlab.docs.client.commands.ResourceDialogListDocumentsCommand;
import org.latexlab.docs.client.commands.SystemAboutCommand;
import org.latexlab.docs.client.commands.SystemApplyCompilerSettingsCommand;
import org.latexlab.docs.client.commands.SystemSetViewCommand;
import org.latexlab.docs.client.commands.SystemToggleFullScreenCommand;
import org.latexlab.docs.client.commands.SystemOpenPageCommand;
import org.latexlab.docs.client.commands.SystemPasteCommand;
import org.latexlab.docs.client.commands.SystemRedoCommand;
import org.latexlab.docs.client.commands.SystemReuseToolbarWindowsCommand;
import org.latexlab.docs.client.commands.SystemSelectResourcesCommand;
import org.latexlab.docs.client.commands.SystemSetResourcesCommand;
import org.latexlab.docs.client.commands.SystemSignOutCommand;
import org.latexlab.docs.client.commands.SystemSpecifyCompilerSettingsCommand;
import org.latexlab.docs.client.commands.SystemToggleColorSyntaxCommand;
import org.latexlab.docs.client.commands.SystemToggleLineNumbersCommand;
import org.latexlab.docs.client.commands.SystemToggleToolbarCommand;
import org.latexlab.docs.client.commands.SystemUndoCommand;
import org.latexlab.docs.client.commands.SystemUploadDocumentsCommand;
import org.latexlab.docs.client.commands.SystemToggleWrapTextCommand;
import org.latexlab.docs.client.dialogs.AboutDialog;
import org.latexlab.docs.client.dialogs.CompilerSettingsDialog;
import org.latexlab.docs.client.dialogs.DialogManager;
import org.latexlab.docs.client.dialogs.ErrorDialog;
import org.latexlab.docs.client.dialogs.FileListDialog;
import org.latexlab.docs.client.dialogs.LoadingDialog;
import org.latexlab.docs.client.dialogs.ResourcesDialog;
import org.latexlab.docs.client.events.CommandEvent;
import org.latexlab.docs.client.events.CommandHandler;
import org.latexlab.docs.client.events.IntRunnable;
import org.latexlab.docs.client.gdocs.DocumentServiceEntry;
import org.latexlab.docs.client.gdocs.DocumentService;
import org.latexlab.docs.client.gdocs.DocumentServiceAsync;
import org.latexlab.docs.client.gdocs.DocumentSignedLocation;
import org.latexlab.docs.client.gdocs.DocumentUser;
import org.latexlab.docs.client.parts.BodyPart;
import org.latexlab.docs.client.parts.FooterPart;
import org.latexlab.docs.client.parts.HeaderPart;
import org.latexlab.docs.client.parts.OutputPart;
import org.latexlab.docs.client.parts.PreviewerPart;
import org.latexlab.docs.client.resources.icons.Icons;
import org.latexlab.docs.client.windows.ToolbarWindow;
import org.latexlab.docs.client.windows.WindowManager;
import org.latexlab.docs.client.windows.toolbars.ToolbarWindowAboveAndBelow;
import org.latexlab.docs.client.windows.toolbars.ToolbarWindowAccents;
import org.latexlab.docs.client.windows.toolbars.ToolbarWindowArrows;
import org.latexlab.docs.client.windows.toolbars.ToolbarWindowArrowsWithCaptions;
import org.latexlab.docs.client.windows.toolbars.ToolbarWindowBinaryOperators;
import org.latexlab.docs.client.windows.toolbars.ToolbarWindowBoundaries;
import org.latexlab.docs.client.windows.toolbars.ToolbarWindowComparison;
import org.latexlab.docs.client.windows.toolbars.ToolbarWindowDiverseSymbols;
import org.latexlab.docs.client.windows.toolbars.ToolbarWindowFormatting;
import org.latexlab.docs.client.windows.toolbars.ToolbarWindowGreekLowercase;
import org.latexlab.docs.client.windows.toolbars.ToolbarWindowGreekUppercase;
import org.latexlab.docs.client.windows.toolbars.ToolbarWindowLogical;
import org.latexlab.docs.client.windows.toolbars.ToolbarWindowMath;
import org.latexlab.docs.client.windows.toolbars.ToolbarWindowOperators;
import org.latexlab.docs.client.windows.toolbars.ToolbarWindowSets;
import org.latexlab.docs.client.windows.toolbars.ToolbarWindowSubscriptAndSuperscript;
import org.latexlab.docs.client.windows.toolbars.ToolbarWindowWhiteSpacesAndDots;
import org.latexlab.docs.editor.advanced.client.parts.EditorPart;
import org.latexlab.docs.editor.advanced.client.parts.MenuPart;
import org.latexlab.docs.editor.advanced.client.parts.ToolbarPart;

import java.util.Date;

/**
 * The GDBE document editor module.
 */
public class DocsAdvancedEditor implements EntryPoint, CommandHandler {

  private final static String CLSI_TOKEN = "8d3id348f34wbvew234sf4df0098";
  private final DocumentServiceAsync docService = GWT.create(DocumentService.class);
  @SuppressWarnings("unused")
  private final LatexLabServiceAsync labService = GWT.create(LatexLabService.class);
  private ClsiRemoteService clsiService;
  private AbsolutePanel root;
  private DialogManager dialogManager;
  private WindowManager windowManager;
  private BodyPart body;
  private EditorPart editor;
  private PreviewerPart previewer;
  private OutputPart output;
  private FlexTable contentPane;
  private HeaderPart header;
  private FooterPart footer;
  private MenuPart menu;
  private ToolbarPart toolbar;
  private DocumentUser currentUser;
  private DocumentServiceEntry currentDocument;
  private DocumentServiceEntry[] allDocuments;
  private ToolbarWindow[] toolbars;
  private IntRunnable controlKeyHandler;
  
  private DocsEditorSettings settings;

  /**
   * Builds and initializes the editor module.
   */
  public void onModuleLoad() {
	clsiService = new ClsiRemoteService();
	docService.getUser(new AsyncCallback<DocumentUser>() {
		@Override
		public void onFailure(Throwable caught) {
		  Window.alert("Authentication Failure: " + caught.getMessage());
		}
		@Override
		public void onSuccess(DocumentUser result) {
  		  if (result == null) {
  		    if (GWT.isClient()) {
  			  //if running in dev mode, auto-authenticate with a test account
  			  docService.setUser("non.gwt.gdata@gmail.com", "CIuortn_DxDp3Zci", new AsyncCallback<DocumentUser>() {
  				  @Override
  				  public void onFailure(Throwable caught) {
  				  }
  				  @Override
  				  public void onSuccess(DocumentUser result) {
  	  		        currentUser = result;
  	                start();
  				  }
  			  });
  			} else {
  		      Window.alert("No login detected. Ensure that any requests go through the server side, " +
  		          "to enforce authentication, rather than directly to the HTML " +
  		          "content.");
  			}
  		  } else {
  		    currentUser = result;
            start();
  		  }
		}
	});
	if (GWT.isClient()) {
	  //if running in dev mode, auto-authenticate with a test account
	  docService.setUser("non.gwt.gdata@gmail.com", "CIuortn_DxDp3Zci", new AsyncCallback<DocumentUser>() {
		  @Override
		  public void onFailure(Throwable caught) {
			checkAuthentication();
		  }
		  @Override
		  public void onSuccess(DocumentUser result) {
			checkAuthentication();
		  }
	  });
	} else {
	  checkAuthentication();
	}
  }
  
  public void checkAuthentication() {
  }
  
  public void start() {
	settings = new DocsEditorSettings();
	dialogManager = new DialogManager();
    contentPane = new FlexTable();
    contentPane.setWidth("100%");
    contentPane.setHeight("100%");
    contentPane.setCellSpacing(0);
    contentPane.setCellPadding(0);
    contentPane.setBorderWidth(0);
    contentPane.insertRow(0);
    contentPane.insertCell(0, 0);
    contentPane.getFlexCellFormatter().setHeight(0, 0, "120px");
    contentPane.insertRow(1);
    contentPane.insertCell(1, 0);
    contentPane.insertRow(2);
    contentPane.insertCell(2, 0);
    contentPane.getFlexCellFormatter().setHeight(2, 0, "20px");
    header = new HeaderPart();
    header.setAuthor(currentUser.getEmail());
    header.addCommandHandler(this);
    footer = new FooterPart();
    footer.addCommandHandler(this);
    menu = new MenuPart();
    menu.addCommandHandler(this);
    toolbar = new ToolbarPart();
    toolbar.addCommandHandler(this);
    editor = new EditorPart();
    editor.addClickHandler(new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
	      try {
		    menu.close();
	      } catch (Exception x) { }
		}
    });
    previewer = new PreviewerPart();
    VerticalPanel headerPanel = new VerticalPanel();
    headerPanel.setWidth("100%");
    headerPanel.add(header);
    headerPanel.add(menu);
    headerPanel.add(toolbar);
    output = new OutputPart();
    output.setOutput("");
    body = new BodyPart();
    body.setWidth("100%");
    body.setHeight("100%");
    body.setTopLeftWidget(this.editor);
    body.setTopRightWidget(this.previewer);
    body.setBottomWidget(this.output);
    contentPane.setWidget(0, 0, headerPanel);
    contentPane.setWidget(1, 0, body);
    contentPane.setWidget(2, 0, footer);
    root = new AbsolutePanel();
    root.setSize("100%", "100%");
    root.add(contentPane);
    FocusPanel wrap = new FocusPanel(root);
    controlKeyHandler = new IntRunnable() {
		@Override
		public void run(int i) {
	      Event e = Event.getCurrentEvent();
	      switch(i) {
	      case 83: //CTRL+S
	    	if (e != null) e.preventDefault();
			execute(new CurrentDocumentSaveCommand());
			break;
		  case 79: //CTRL+O
	    	if (e != null) e.preventDefault();
			execute(new ExistingDocumentOpenCommand());
			break;
		  case 78: //CTRL+N
	    	if (e != null) e.preventDefault();
			execute(new NewDocumentStartCommand());
			break;
		  }
		}
    };
    wrap.addKeyDownHandler(new KeyDownHandler() {
		@Override
		public void onKeyDown(KeyDownEvent event) {
		  if (event.isControlKeyDown()) {
			controlKeyHandler.run(event.getNativeKeyCode());
		  }
		}
    });
    RootPanel.get().add(wrap);
    
    windowManager = new WindowManager(root);
    PickupDragController dragController = new PickupDragController(root, true);
    dragController.setBehaviorConstrainedToBoundaryPanel(true);
    dragController.setBehaviorMultipleSelection(false);
    dragController.setBehaviorDragStartSensitivity(1);
    toolbars = new ToolbarWindow[17];
    execute(new SystemApplyCompilerSettingsCommand());
	settings.setHasCompilerSettings(false);
    execute(new SystemToggleToolbarCommand(ToolbarWindowFormatting.TITLE));
    loadDocument();
  }
  
  /**
   * Extracts the "docid" parameter from the URL querystring and loads the
   * respective document - the document details are loaded first, followed by
   * the document contents.
   * If "docid" is blank then a new, unsaved, document is loaded.
   */
  public void loadDocument() {
    String documentId = Window.Location.getParameter("docid");
    if (documentId == null || documentId.equals("")) {
      execute(new NewDocumentLoadCommand());
    } else {
      execute(new ExistingDocumentLoadCommand(documentId));
    }
  }
  
  /**
   * Closes the current window.
   */
  private native void close() /*-{
    $wnd.close();
  }-*/;

  /**
   * Sets the currently open document and updates the header info and window title
   * with the respective document information.
   * 
   * @param doc the document which to use
   */
  private void setDocument(DocumentServiceEntry doc) {
    currentDocument = doc;
    header.setTitle(doc.getTitle());
    Window.setTitle(doc.getTitle() + " - LaTeX Lab");
    if (currentDocument.isStored()) {
      header.setInfo(currentDocument.getDocumentId(), currentDocument.getEdited(), currentDocument.getEditor());
    }
  }
  
  /**
   * Shows a status message. If modal, a loading dialog is displayed, otherwise
   * the header status area is used.
   * 
   * @param message the status message to display
   * @param modal whether the status display is modal
   */
  private void showStatus(String message, boolean modal) {
    if (modal) {
      LoadingDialog loadingDialog = LoadingDialog.get(this);
      loadingDialog.setMessage(message);
      dialogManager.showDialog(loadingDialog);
    } else {
      header.setStatus(message);
    }
  }
  
  /**
   * Clears and hides any visible status messages.
   */
  private void clearStatus() {
	LoadingDialog.get(this).hide();
    header.setStatus("");
  }

  /**
   * Handles a command error.
   * 
   * @param error the error exception
   * @param command the command that triggered the error
   * @param alternate the command to trigger on cancel
   * @param retryAttempts the number of times to retry the command before displaying an error dialog
   */
  private <E extends Command, V extends Command> void handleError(
	    Throwable error, E command, V alternate, int retryAttempts){
    clearStatus();
    if (command.getAttemptCount() <= retryAttempts) {
      command.newAttempt();
      execute(command);
    } else {
      ErrorDialog errorDialog = ErrorDialog.get(this);
      errorDialog.update(error, command, alternate);
      dialogManager.showDialog(errorDialog);
    }
  }
  
  /**
   * Handles a command event. This is where all the components converge along with
   * the application logic.
   * 
   * @param the command event
   */
  
  public void onCommand(CommandEvent e) {
	execute(e.getCommand());
  }
  
  /**
   * Handles a command event. This is where all the components converge along with
   * the application logic.
   * 
   * @param the command event
   */
  public <T extends Command> void execute(final T cmd) {
    Date now = new Date();
    switch (cmd.getCommandId()) {
      case ResourceDialogListDocumentsCommand.serialUid:
    	ResourceDialogListDocumentsCommand rdldCmd = (ResourceDialogListDocumentsCommand) cmd;
    	if (rdldCmd.isUseCache() && allDocuments != null) {
    		ResourcesDialog.get(DocsAdvancedEditor.this).setEntries(allDocuments, currentDocument.getDocumentId());
    	} else {
          docService.getDocuments(false, new AsyncCallback<DocumentServiceEntry[]>() {
            public void onFailure(Throwable caught) {
              handleError(caught, cmd, null, 1);
            }
            public void onSuccess(DocumentServiceEntry[] result) {
              allDocuments = result;
        	  ResourcesDialog.get(DocsAdvancedEditor.this).setEntries(result, currentDocument.getDocumentId());
            }
        });
    	}
        break;
      case FileDialogUnstarDocumentCommand.serialUid:
    	FileDialogUnstarDocumentCommand fdudCmd = ((FileDialogUnstarDocumentCommand)cmd);
	    docService.setDocumentStarred(fdudCmd.getDocumentId(),
	        false,
	        new AsyncCallback<Boolean>(){
	          public void onFailure(Throwable caught) {
	            handleError(caught, cmd, null, 1);
	          }
	          public void onSuccess(Boolean result) {
	          }
	    });
	    break;
      case FileDialogStarDocumentCommand.serialUid:
    	FileDialogStarDocumentCommand fdsdCmd = ((FileDialogStarDocumentCommand)cmd);
        docService.setDocumentStarred(
            fdsdCmd.getDocumentId(),
            true,
            new AsyncCallback<Boolean>(){
              public void onFailure(Throwable caught) {
                handleError(caught, cmd, null, 1);
              }
              public void onSuccess(Boolean result) {
              }
            }
        );
        break;
      case FileDialogListDocumentsCommand.serialUid:
    	FileDialogListDocumentsCommand fdldCmd = (FileDialogListDocumentsCommand) cmd;
    	if (fdldCmd.isUseCache() && allDocuments != null) {
    	  FileListDialog fileListDialog = FileListDialog.get(DocsAdvancedEditor.this);
          fileListDialog.setEntries(allDocuments);
          fileListDialog.showEntries();
    	} else {
          docService.getDocuments(false, new AsyncCallback<DocumentServiceEntry[]>() {
            public void onFailure(Throwable caught) {
              handleError(caught, cmd, null, 1);
            }
            public void onSuccess(DocumentServiceEntry[] result) {
              allDocuments = result;
        	  FileListDialog fileListDialog = FileListDialog.get(DocsAdvancedEditor.this);
              fileListDialog.setEntries(result);
              fileListDialog.showEntries();
            }
          });
    	}
        break;
      case FileDialogOpenDocumentCommand.serialUid:
    	FileDialogOpenDocumentCommand fdodCmd = (FileDialogOpenDocumentCommand) cmd;
        Window.open("http://docs.google.com/Doc?docid=" + fdodCmd.getDocumentId() + "&hl=en", fdodCmd.getDocumentId(), "");
        break;
      case NewDocumentStartCommand.serialUid:
        Window.open("/docs", "Untitled" + now.getTime(), "");
        break;
      case ExistingDocumentOpenCommand.serialUid:
    	dialogManager.showDialog(FileListDialog.get(this));
        break;
      case CurrentDocumentExportCommand.serialUid:
    	if (!settings.getHasCompilerSettings()) {
    	  settings.setHasCompilerSettings(true);
    	  CompilerSettingsDialog.get(this, cmd).center();
    	  return;
    	}
    	CurrentDocumentExportCommand cdeCmd = (CurrentDocumentExportCommand) cmd;
        showStatus("Exporting document...", false);
        compile(cdeCmd.getExportFormat(),
    	    new CompileCallback() {
    		@Override
    		public void onFailure(Throwable caught) {
              handleError(caught, cmd, null, 0);
    		}
    		@Override
    		public void onSuccess(ClsiServiceCompileResponse result) {
    		  String r = String.valueOf(new Date().getTime());
			  if (result.getOutputErrors().length == 0 && result.getOutputFiles().length > 0) {
    			String url = result.getOutputFiles()[0].getUrl() + "?r=" + r;
    			Window.open(url, currentDocument.getDocumentId() + "_pdf", "");
    		  }
    		  if (result.getOutputLogs().length > 0) {
      		    output.setUrl(result.getOutputLogs()[0].getUrl() + "?r=" + r);
    		    body.setVerticalSplitPosition((Window.getClientHeight() - 300) + "px");
    		  } else if (result.getOutputErrors().length > 0) {
    			output.setOutput(result.getOutputErrors()[0].getMessage());
    		    body.setVerticalSplitPosition((Window.getClientHeight() - 300) + "px");
    		  }
              clearStatus();
    		}
    	});
      	break;
      case CurrentDocumentCompileCommand.serialUid:
      	if (!settings.getHasCompilerSettings()) {
      	  settings.setHasCompilerSettings(true);
      	  CompilerSettingsDialog.get(this, cmd).center();
      	  return;
      	}
        showStatus("Compiling document...", false);
        previewer.clear();
        output.clear();
        compile("png", new CompileCallback() {
    		@Override
    		public void onFailure(Throwable caught) {
              handleError(caught, cmd, null, 0);
    		}
    		@Override
    		public void onSuccess(ClsiServiceCompileResponse result) {
    		  String r = String.valueOf(new Date().getTime());
    		  if (result.getOutputErrors().length == 0 && result.getOutputFiles().length > 0) {
    			String[] urls =  new String[result.getOutputFiles().length];
    			for (int i=0; i<urls.length; i++) {
    			  urls[i] = result.getOutputFiles()[i].getUrl() + "?r=" + r;
    			}
    		    previewer.setUrls(urls);
    		    body.setHorizontalSplitPosition("50%");
    		  } else {
    		    previewer.setUrls(new String[] { "/images/error.png" });
    		    body.setHorizontalSplitPosition("50%");
    		  }
    		  if (result.getOutputLogs().length > 0) {
      		    output.setUrl(result.getOutputLogs()[0].getUrl() + "?r=" + r);
    		    body.setVerticalSplitPosition((Window.getClientHeight() - 300) + "px");
    		  } else if (result.getOutputErrors().length > 0) {
    			output.setOutput(result.getOutputErrors()[0].getMessage());
    		    body.setVerticalSplitPosition((Window.getClientHeight() - 300) + "px");
    		  }
              clearStatus();
    		}
    	});
        break;
      case CurrentDocumentCopyCommand.serialUid:
        showStatus("Copying document...", true);
        docService.createDocument("Copy of " + currentDocument.getTitle(), editor.getText(),
            new AsyncCallback<DocumentServiceEntry>(){
              public void onFailure(Throwable caught) {
                handleError(caught, cmd, null, 0);
              }
              public void onSuccess(DocumentServiceEntry result) {
                clearStatus();
                Window.open("/docs?docid=" + result.getDocumentId(), result.getDocumentId(), "");
              }
        });
        break;
      case CurrentDocumentSaveCommand.serialUid:
        showStatus("Saving...", false);
        docService.saveDocument(currentDocument.getDocumentId(), currentDocument.getEtag(), currentDocument.getTitle(),
            editor.getText(), new AsyncCallback<DocumentServiceEntry>() {
          public void onFailure(Throwable caught) {
            handleError(caught, cmd, null, 0);
          }
          public void onSuccess(DocumentServiceEntry result) {
            setDocument(result);
            clearStatus();
          }
        });
        break;
      case CurrentDocumentSaveAndCloseCommand.serialUid:
        showStatus("Saving document...", true);
        docService.saveDocument(currentDocument.getDocumentId(), currentDocument.getEtag(), currentDocument.getTitle(),
        	editor.getText(), new AsyncCallback<DocumentServiceEntry>() {
          public void onFailure(Throwable caught) {
            handleError(caught, cmd, null, 0);
          }
          public void onSuccess(DocumentServiceEntry result) {
            setDocument(result);
            clearStatus();
            close();
          }
        });
        break;
      case CurrentDocumentCloseCommand.serialUid:
        close();
        break;
      case CurrentDocumentRenameCommand.serialUid:
        final String newName = Window.prompt("Enter new document name:", currentDocument.getTitle());
        if (newName != null && !newName.equals("")) {
          if (currentDocument.isStored()) {
            showStatus("Renaming...", false);
            docService.renameDocument(currentDocument.getDocumentId(), newName,
                new AsyncCallback<DocumentServiceEntry>(){
              public void onFailure(Throwable caught) {
                handleError(caught, cmd, null, 0);
              }
              public void onSuccess(DocumentServiceEntry result) {
                setDocument(result);
                clearStatus();
              }
            });
          } else {
            currentDocument.setTitle(newName);
            setDocument(currentDocument);
          }
        }
        break;
      case CurrentDocumentDeleteCommand.serialUid:
        if (Window.confirm("This document will be deleted and closed.")) {
          if (currentDocument.isStored()) {
            showStatus("Deleting document...", true);
            docService.deleteDocument(currentDocument.getDocumentId(), currentDocument.getEtag(),
                  new AsyncCallback<Boolean>() {
              public void onFailure(Throwable caught) {
                handleError(caught, cmd, null, 0);
              }
              public void onSuccess(Boolean result) {
                clearStatus();
                close();
              }
            });
          } else {
            close();
          }
        }
        break;
      case CurrentDocumentLoadContentsCommand.serialUid:
        showStatus("Loading document contents...", true);
        docService.getDocumentContents(currentDocument.getDocumentId(),
            new AsyncCallback<String>() {
          public void onFailure(Throwable caught) {
            handleError(caught, cmd, null, 1);
          }
          public void onSuccess(String result) {
            clearStatus();
            loadEditor(result);
          }
        });
        break;
      case CurrentDocumentRevisionHistoryCommand.serialUid:
        Window.open("http://docs.google.com/Revs?id=" +
            currentDocument.getDocumentId() + "&tab=revlist",
            currentDocument.getDocumentId(), "");
        break;
      case SystemUploadDocumentsCommand.serialUid:
        Window.open("http://docs.google.com/DocAction?action=updoc&hl=en",
            "UploadDocuments", "");
        break;
      case NewDocumentLoadCommand.serialUid:
    	docService.getNewDocument(new AsyncCallback<DocumentServiceEntry>() {
			@Override
			public void onFailure(Throwable caught) {
              handleError(caught, cmd, null, 1);
			}
			@Override
			public void onSuccess(DocumentServiceEntry result) {
		      setDocument(result);
		      loadEditor("\\documentclass[12pt]{article}\n\\usepackage[utf8]{inputenc}\n\\usepackage{amsmath}\n\\title{\\LaTeX}\n\\date{}\n\\begin{document}\n  \\maketitle \n  \\LaTeX{} is a document preparation system for the \\TeX{} \n  typesetting program. It offers programmable desktop publishing \n  features and extensive facilities for automating most aspects of \n  typesetting and desktop publishing, including numbering and \n  cross-referencing, tables and figures, page layout, bibliographies, \n  and much more. \\LaTeX{} was originally written in 1984 by Leslie \n  Lamport and has become the dominant method for using \\TeX; few \n  people write in plain \\TeX{} anymore. The current version is \n  \\LaTeXe.\n \n  % This is a comment; it is not shown in the final output.\n  % The following shows a little of the typesetting power of LaTeX\n  \\begin{align}\n    E &= mc^2                              \\\\\n    m &= \\frac{m_0}{\\sqrt{1-\\frac{v^2}{c^2}}}\n  \\end{align}\n\\end{document}\n");
		      //loadEditor("\\documentclass[11pt]{article}\n\\usepackage[utf8]{inputenc}\n\\author{Bobby Soares}\n\\title{Untitled Document}\n\\date{" + (new Date()).toString() + "}\n\\begin{document}\n\\maketitle\n\nHello LaTeX Lab!! :)\n\n\\end{document}\n");
			}
    	});
        break;
      case ExistingDocumentLoadCommand.serialUid:
    	final ExistingDocumentLoadCommand edlCmd = (ExistingDocumentLoadCommand) cmd;
        showStatus("Loading document...", true);
        docService.getDocument(edlCmd.getDocumentId(), new AsyncCallback<DocumentServiceEntry>() {
          public void onFailure(Throwable caught) {
            handleError(caught, cmd, new NewDocumentLoadCommand(), 1);
          }
          public void onSuccess(DocumentServiceEntry result) {
        	if (result == null) {
        	  handleError(new Exception("No document found with the ID " + edlCmd.getDocumentId()), cmd, new NewDocumentLoadCommand(), 0);
        	} else {
              setDocument(result);
              clearStatus();
              execute(new CurrentDocumentLoadContentsCommand());
        	}
          }
        });
        break;
      case SystemSetResourcesCommand.serialUid:
    	SystemSetResourcesCommand ssrCmd = (SystemSetResourcesCommand) cmd;
    	settings.setResources(ssrCmd.getResources());
    	settings.setPrimaryResource(ssrCmd.getPrimaryResource());
    	break;
      case SystemReuseToolbarWindowsCommand.serialUid:
    	settings.setReuseToolbarWindows(!settings.isReuseToolbarWindows());
    	if (settings.isReuseToolbarWindows()) {
    	  menu.setMenuItemIcon("Reuse toolbar windows", Icons.editorIcons.CheckBlack());
    	} else {
      	  menu.setMenuItemIcon("Reuse toolbar windows", Icons.editorIcons.Blank());
    	}
    	break;
      case SystemAboutCommand.serialUid:
    	dialogManager.showDialog(AboutDialog.get(this));
        break;
      case SystemSpecifyCompilerSettingsCommand.serialUid:
    	dialogManager.showDialog(CompilerSettingsDialog.get(this, null));
    	break;
      case SystemSelectResourcesCommand.serialUid:
    	ResourcesDialog.get(this).center();
    	break;
      case SystemApplyCompilerSettingsCommand.serialUid:
    	SystemApplyCompilerSettingsCommand sacsCmd = (SystemApplyCompilerSettingsCommand) cmd;
    	if (sacsCmd.isUseDefault()) {
    	  settings.setClsiAsyncPath("http://dev-clsi.latexlab.org/out/async/");
    	  settings.setClsiServiceToken(CLSI_TOKEN);
    	  settings.setClsiServiceId(currentUser.getToken());
    	  settings.setClsiServiceUrl("http://dev-clsi.latexlab.org/service.php");
    	  settings.setCompilerName("latex");
    	} else {
    	  settings.setClsiAsyncPath(sacsCmd.getClsiAsyncPath());
    	  settings.setClsiServiceToken(sacsCmd.getClsiServiceToken());
    	  settings.setClsiServiceUrl(sacsCmd.getClsiServiceUrl());
    	  settings.setCompilerName(sacsCmd.getCompilerName());
    	}
    	clsiService.setAsyncPath(settings.getClsiAsyncPath());
    	clsiService.setServiceUrl(settings.getClsiServiceUrl());
    	clsiService.setToken(settings.getClsiServiceToken());
    	clsiService.setId(settings.getClsiServiceId());
    	settings.setHasCompilerSettings(true);
    	break;
      case SystemToggleFullScreenCommand.serialUid:
        boolean isFullScreen = !header.isVisible();
        if (isFullScreen) {
          menu.setMenuItemIcon("Full-screen mode", Icons.editorIcons.Blank());
          header.setVisible(true);
          contentPane.getFlexCellFormatter().setHeight(0, 0, "120px");
		  body.setVerticalSplitPosition("100%");
        } else {
          menu.setMenuItemIcon("Full-screen mode", Icons.editorIcons.CheckBlack());
          header.setVisible(false);
          contentPane.getFlexCellFormatter().setHeight(0, 0, "40px");
		  body.setVerticalSplitPosition("100%");
        }
        break;
      case SystemToggleToolbarCommand.serialUid:
    	SystemToggleToolbarCommand sttCmd = (SystemToggleToolbarCommand) cmd;
    	int toolbarIndex = getToolbarIndex(sttCmd.getName());
    	if (toolbarIndex >= 0) {
    	  ToolbarWindow toolbar = this.toolbars[toolbarIndex];
          boolean toVisible = !toolbar.isVisible();
    	  if (settings.isReuseToolbarWindows()) {
    	    for (int i=0; i<toolbars.length; i++) {
    	      if (toolbars[i] != null) {
    	        toolbars[i].hide();
    	      }
    	      DocsAdvancedEditor.this.toolbar.setButtonState(i + 6, false);
    	    }
    	  } else {
    	    toolbar.hide();
    	    DocsAdvancedEditor.this.toolbar.setButtonState(toolbarIndex + 6, false);
    	  }
    	  if (toVisible) {
    	    toolbar.show();
    	    DocsAdvancedEditor.this.toolbar.setButtonState(toolbarIndex + 6, true);
    	  }
    	}
    	break;
      case SystemToggleColorSyntaxCommand.serialUid:
    	if (editor.getColorSyntax()) {
    	  menu.setMenuItemIcon("Highlight Syntax", Icons.editorIcons.Blank());
    	  editor.setColorSyntax(false);
    	  settings.setColorSyntax(false);
    	} else {
    	  menu.setMenuItemIcon("Highlight Syntax", Icons.editorIcons.CheckBlack());
    	  editor.setColorSyntax(true);
    	  settings.setColorSyntax(true);
    	}
    	break;
      case SystemToggleLineNumbersCommand.serialUid:
    	if (editor.getShowLineNumbers()) {
    	  menu.setMenuItemIcon("Show Line Numbers", Icons.editorIcons.Blank());
    	  editor.setShowLineNumbers(false);
    	  settings.setShowLineNumbers(false);
    	} else {
    	  menu.setMenuItemIcon("Show Line Numbers", Icons.editorIcons.CheckBlack());
    	  editor.setShowLineNumbers(true);
    	  settings.setShowLineNumbers(true);
    	}
    	break;
      case SystemToggleWrapTextCommand.serialUid:
    	if (editor.getWrapText()) {
    	  menu.setMenuItemIcon("Wrap Text", Icons.editorIcons.Blank());
    	  editor.setWrapText(false);
    	  settings.setWrapText(false);
    	} else {
    	  menu.setMenuItemIcon("Wrap Text", Icons.editorIcons.CheckBlack());
    	  editor.setWrapText(true);
    	  settings.setWrapText(true);
    	}
    	break;
      case SystemSignOutCommand.serialUid:
        showStatus("Signing out...", true);
        docService.logout(new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
	          handleError(caught, cmd, null, 1);
			}
			@Override
			public void onSuccess(String result) {
              clearStatus();
              Window.Location.replace(((SystemSignOutCommand)cmd).getReturnUrl());
			}
        });
        break;
      case SystemPasteCommand.serialUid:
    	SystemPasteCommand spCmd = (SystemPasteCommand) cmd;
    	editor.replaceSelection(spCmd.getText());
    	break;
      case SystemUndoCommand.serialUid:
    	editor.undo();
    	break;
      case SystemRedoCommand.serialUid:
    	editor.redo();
    	break;
      case SystemOpenPageCommand.serialUid:
    	SystemOpenPageCommand sopCmd = (SystemOpenPageCommand) cmd;
    	Window.open(sopCmd.getUrl(), sopCmd.getName(), "");
    	break;
      case SystemSetViewCommand.serialUid:
    	SystemSetViewCommand ssvCmd = (SystemSetViewCommand) cmd;
    	setPerspective(ssvCmd.getView());
    	break;
      case CurrentDocumentCompileLocalCommand.serialUid:
    	Window.alert("Starting local service...");
    	final ClsiLocalService svc = new ClsiLocalService("/editor/");
    	svc.start(new ReadyCallback() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("On ready failure: " + caught.getMessage());
			}
			@Override
			public void onSuccess(Boolean result) {
				Window.alert("On ready succeeded.");
		    	svc.compile("name", "contents", "primary", null, "compiler", "format", new CompileCallback() {
					@Override
					public void onFailure(Throwable caught) {
					  Window.alert("Local compile command failed: " + caught.getMessage());
					}
					@Override
					public void onSuccess(ClsiServiceCompileResponse result) {
					  Window.alert("Local compile command succeeded.");
					}
		    	});
			}
    	});
    	break;
      default:
        Window.alert("Not implemented");
        break;
    }
  }
  
  private int getToolbarIndex(String name) {
	if (name.equalsIgnoreCase(ToolbarWindowAboveAndBelow.TITLE)) {
	  if (toolbars[0] == null) toolbars[0] = ToolbarWindowAboveAndBelow.get(this, windowManager);
	  return 0;
	} else if (name.equalsIgnoreCase(ToolbarWindowAccents.TITLE)) {
	  if (toolbars[1] == null) toolbars[1] = ToolbarWindowAccents.get(this, windowManager);
	  return 1;
	} else if (name.equalsIgnoreCase(ToolbarWindowArrows.TITLE)) {
	  if (toolbars[2] == null) toolbars[2] = ToolbarWindowArrows.get(this, windowManager);
	  return 2;
	} else if (name.equalsIgnoreCase(ToolbarWindowArrowsWithCaptions.TITLE)) {
	  if (toolbars[3] == null) toolbars[3] = ToolbarWindowArrowsWithCaptions.get(this, windowManager);
	  return 3;
	} else if (name.equalsIgnoreCase(ToolbarWindowBinaryOperators.TITLE)) {
	  if (toolbars[4] == null) toolbars[4] = ToolbarWindowBinaryOperators.get(this, windowManager);
	  return 4;
	} else if (name.equalsIgnoreCase(ToolbarWindowBoundaries.TITLE)) {
	  if (toolbars[5] == null) toolbars[5] = ToolbarWindowBoundaries.get(this, windowManager);
	  return 5;
	} else if (name.equalsIgnoreCase(ToolbarWindowComparison.TITLE)) {
	  if (toolbars[6] == null) toolbars[6] = ToolbarWindowComparison.get(this, windowManager);
	  return 6;
	} else if (name.equalsIgnoreCase(ToolbarWindowDiverseSymbols.TITLE)) {
	  if (toolbars[7] == null) toolbars[7] = ToolbarWindowDiverseSymbols.get(this, windowManager);
	  return 7;
	} else if (name.equalsIgnoreCase(ToolbarWindowFormatting.TITLE)) {
	  if (toolbars[8] == null) toolbars[8] = ToolbarWindowFormatting.get(this, windowManager);
	  return 8;
	} else if (name.equalsIgnoreCase(ToolbarWindowGreekLowercase.TITLE)) {
	  if (toolbars[9] == null) toolbars[9] = ToolbarWindowGreekLowercase.get(this, windowManager);
	  return 9;
	} else if (name.equalsIgnoreCase(ToolbarWindowGreekUppercase.TITLE)) {
	  if (toolbars[10] == null) toolbars[10] = ToolbarWindowGreekUppercase.get(this, windowManager);
	  return 10;
	} else if (name.equalsIgnoreCase(ToolbarWindowLogical.TITLE)) {
	  if (toolbars[11] == null) toolbars[11] = ToolbarWindowLogical.get(this, windowManager);
	  return 11;
	} else if (name.equalsIgnoreCase(ToolbarWindowMath.TITLE)) {
	  if (toolbars[12] == null) toolbars[12] = ToolbarWindowMath.get(this, windowManager);
	  return 12;
	} else if (name.equalsIgnoreCase(ToolbarWindowOperators.TITLE)) {
	  if (toolbars[13] == null) toolbars[13] = ToolbarWindowOperators.get(this, windowManager);
	  return 13;
	} else if (name.equalsIgnoreCase(ToolbarWindowSets.TITLE)) {
	  if (toolbars[14] == null) toolbars[14] = ToolbarWindowSets.get(this, windowManager);
	  return 14;
	} else if (name.equalsIgnoreCase(ToolbarWindowSubscriptAndSuperscript.TITLE)) {
	  if (toolbars[15] == null) toolbars[15] = ToolbarWindowSubscriptAndSuperscript.get(this, windowManager);
	  return 15;
	} else if (name.equalsIgnoreCase(ToolbarWindowWhiteSpacesAndDots.TITLE)) {
	  if (toolbars[16] == null) toolbars[16] = ToolbarWindowWhiteSpacesAndDots.get(this, windowManager);
	  return 16;
	}
	return -1;
  }

  private void getResourceReferences(final AsyncCallback<ClsiResourceReference[]> callback) {
	final int size = settings.getResources().size();
	String[] docIds = new String[size];
	for (int i=0; i<size; i++) {
	  docIds[i] = settings.getResources().get(i).getContentLink();
	}
	docService.getDocumentContentUrls(docIds,
	    new AsyncCallback<DocumentSignedLocation[]>() {
		  @Override
		  public void onFailure(Throwable caught) {
		    callback.onFailure(caught);
		  }
		  @Override
		  public void onSuccess(DocumentSignedLocation[] result) {
			  ClsiResourceReference[] refs = new ClsiResourceReference[size];
		    for (int i=0; i<size; i++) {
		      DocumentSignedLocation dsl = result[i];
		      DocumentServiceEntry entry = settings.getResources().get(i);
		      String enc = null;
		      if (entry.getType().equalsIgnoreCase("document")) {
		    	enc = "UTF-8";
		      }
		      refs[i] = ClsiResourceReference.newInstance(entry.getDocumentId(),
		          entry.getTitle(), dsl.getUrl(), dsl.getAuthorization(),
		          entry.getContentType(), enc, entry.getEdited());
		    }
		    callback.onSuccess(refs);
		  }
	});
  }
  
  private void loadEditor(final String value) {
    showStatus("Loading advanced editor...", true);
    editor.addLoadHandler(new LoadHandler() {
        @Override
	    public void onLoad(LoadEvent event) {
          editor.setText(value);
          clearStatus();
	    }
    });
    editor.init(settings.isColorSyntax(), settings.isWrapText(),
        settings.isShowLineNumbers(), controlKeyHandler);
  }
  
  private void compile(final String format, final CompileCallback callback) {
    getResourceReferences(new AsyncCallback<ClsiResourceReference[]>() {
		@Override
		public void onFailure(Throwable caught) {
		  callback.onFailure(caught);
		}
		@Override
		public void onSuccess(ClsiResourceReference[] result) {
		  String primary = currentDocument.getTitle();
		  if (settings.getPrimaryResource() != null) {
		    primary = settings.getPrimaryResource().getTitle();
		  }
	      clsiService.compile(currentDocument.getTitle(), editor.getText(),
	          primary, result, "latex", format, callback);
		}
    });
  }
  
  private void setPerspective(int view) {
  	switch(view) {
  	case SystemSetViewCommand.VIEW_SOURCE:
  	  body.setHorizontalSplitPosition("100%");
  	  body.setVerticalSplitPosition("100%");
      break;
  	case SystemSetViewCommand.VIEW_PREVIEW:
      body.setHorizontalSplitPosition("0%");
      body.setVerticalSplitPosition("100%");
      break;
  	case SystemSetViewCommand.VIEW_SPLIT:
      body.setHorizontalSplitPosition("50%");
      body.setVerticalSplitPosition("100%");
      break;
  	case SystemSetViewCommand.VIEW_OUTPUT:
      body.setHorizontalSplitPosition("100%");
      body.setVerticalSplitPosition("0%");
      break;
  	}
  }
  
}