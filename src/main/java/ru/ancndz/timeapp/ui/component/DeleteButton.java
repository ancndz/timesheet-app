package ru.ancndz.timeapp.ui.component;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;

/**
 * Кнопка удаления.
 *
 * @author Anton Utkaev
 * @since 2024.05.01
 */
public class DeleteButton extends Button {

    private DeleteButton() {
        super(VaadinIcon.TRASH.create());
        addThemeVariants(ButtonVariant.LUMO_ERROR);
    }

    public static Builder newButton() {
        return new Builder();
    }

    public static class Builder {

        private String mainText;

        private ComponentEventListener<ConfirmDialog.ConfirmEvent> confirmListener;

        private ComponentEventListener<ConfirmDialog.CancelEvent> cancelListener = e -> {
        };

        private boolean closeParentDialog = false;

        private Builder() {
        }

        public Builder withMainText(String mainText) {
            this.mainText = mainText;
            return this;
        }

        public Builder withConfirmListener(ComponentEventListener<ConfirmDialog.ConfirmEvent> confirmListener) {
            this.confirmListener = confirmListener;
            return this;
        }

        public Builder withCancelListener(ComponentEventListener<ConfirmDialog.CancelEvent> cancelListener) {
            this.cancelListener = cancelListener;
            return this;
        }

        public Builder closingParentDialog() {
            this.closeParentDialog = true;
            return this;
        }

        public DeleteButton build() {
            final DeleteButton deleteButton = new DeleteButton();
            deleteButton.addClickListener(buttonClickEvent -> {
                final ConfirmDialog confirmDialog =
                        new ConfirmDialog(buttonClickEvent.getSource().getTranslation("app.confirm.delete"),
                                this.mainText,
                                buttonClickEvent.getSource().getTranslation("app.button.delete"),
                                this.confirmListener,
                                buttonClickEvent.getSource().getTranslation("app.button.cancel"),
                                this.cancelListener);

                if (this.closeParentDialog) {
                    confirmDialog.addConfirmListener(confirmEvent -> {
                        buttonClickEvent.getSource()
                                .getParent()
                                .filter(Dialog.class::isInstance)
                                .map(Dialog.class::cast)
                                .ifPresent(Dialog::close);
                    });
                }
                confirmDialog.setConfirmButtonTheme("error primary");
                confirmDialog.open();
            });
            return deleteButton;
        }
    }

}
