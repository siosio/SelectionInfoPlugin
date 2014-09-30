package siosio.intellij.plugin;

import java.nio.charset.Charset;
import java.util.zip.CheckedInputStream;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.encoding.EncodingManager;

public class EditorSelectedInfo {

    private final Editor editor;

    public EditorSelectedInfo(Editor editor) {
        this.editor = editor;
    }

    public int getSelectedLineCount() {
        SelectionModel selectionModel = getSelectionModel();
        if (!selectionModel.hasSelection()) {
            return 0;
        }
        VisualPosition startPosition = selectionModel.getSelectionStartPosition();
        VisualPosition endPosition = selectionModel.getSelectionEndPosition();
        if ((startPosition == null) || (endPosition == null)) {
            return 0;
        }
        return (endPosition.line - startPosition.line) + 1;
    }

    public int getCharCount() {
        return getSelectionText().length();
    }

    public int getByteCount() {
        Document document = editor.getDocument();
        VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(document);
        Charset charset = EncodingManager.getInstance().getCachedCharsetFromContent(document);
        if (charset == null) {
            charset = virtualFile != null ? virtualFile.getCharset() : null;
        }
        if (charset == null) {
            return 0;
        }
        return getSelectionText().getBytes(charset).length;
    }

    private String getSelectionText() {
        SelectionModel selectionModel = getSelectionModel();
        if (!selectionModel.hasSelection()) {
            return "";
        }
        String selectedText = selectionModel.getSelectedText();
        if (selectedText == null) {
            return "";
        }
        return selectedText;
    }

    private SelectionModel getSelectionModel() {
        return editor.getSelectionModel();
    }

}
