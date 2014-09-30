package siosio.intellij.plugin;

import java.awt.event.MouseEvent;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.CaretEvent;
import com.intellij.openapi.editor.event.CaretListener;
import com.intellij.openapi.editor.event.EditorEventMulticaster;
import com.intellij.openapi.editor.event.SelectionEvent;
import com.intellij.openapi.editor.event.SelectionListener;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.StatusBarWidget.TextPresentation;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.openapi.wm.impl.status.EditorBasedWidget;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShowSelectedInfo implements ProjectComponent {

    private final Project project;

    public ShowSelectedInfo(Project project) {
        this.project = project;
    }

    @Override
    public void projectOpened() {
        WindowManager windowManager = WindowManager.getInstance();
        StatusBar statusBar = windowManager.getStatusBar(project);
        if (statusBar == null) {
            return;
        }
        statusBar.addWidget(new MyEditorBasedWidget(project));
    }

    @Override
    public void projectClosed() {
    }

    @Override
    public void initComponent() {
    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "ShowSelectedInfo";
    }

    private static class MyEditorBasedWidget extends EditorBasedWidget
            implements StatusBarWidget.Multiframe, TextPresentation, SelectionListener {

        protected MyEditorBasedWidget(@NotNull Project project) {
            super(project);
        }

        @NotNull
        @Override
        public String ID() {
            return "selectedinfo";
        }

        @Nullable
        @Override
        public WidgetPresentation getPresentation(@NotNull PlatformType type) {
            return this;
        }

        @Override
        public void selectionChanged(@NotNull FileEditorManagerEvent event) {
            update(this.getEditor());
        }

        @Override
        public void install(@NotNull StatusBar bar) {
            super.install(bar);
            EditorEventMulticaster eventMulticaster = EditorFactory.getInstance().getEventMulticaster();
            eventMulticaster.addSelectionListener(this, this);
        }

        private String statusBarText = "";

        @Override
        public void selectionChanged(SelectionEvent event) {
            update(event.getEditor());
        }

        private int getCharCount(String value) {
            if (value == null) {
                return 0;
            }
            int result = 0;
            for (char c : value.toCharArray()) {
                if (!Character.isWhitespace(c)) {
                    result++;
                }
            }
            return result;
        }

        @NotNull
        @Override
        public String getText() {
            return statusBarText;
        }

        @NotNull
        @Override
        public String getMaxPossibleText() {
            return "00000000000000000000";
        }

        @Override
        public float getAlignment() {
            return 5f;
        }

        @Nullable
        @Override
        public String getTooltipText() {
            return null;
        }

        @Nullable
        @Override
        public Consumer<MouseEvent> getClickConsumer() {
            return null;
        }

        @Override
        public StatusBarWidget copy() {
            return new MyEditorBasedWidget(myProject);
        }

        private void update(Editor editor) {
            if (editor.isDisposed()) {
                return;
            }
            EditorSelectedInfo selectedInfo = new EditorSelectedInfo(editor);
            StringBuilder result = new StringBuilder();
            result.append("line count:");
            result.append(selectedInfo.getSelectedLineCount());
            result.append(", char count:");
            result.append(selectedInfo.getCharCount());
            result.append(", byte count:");
            result.append(selectedInfo.getByteCount());
            statusBarText = result.toString();

            myStatusBar.updateWidget(ID());
        }
    }
}
