package ru.ancndz.timeapp.ui.component;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * Кнопка переключения темы.
 *
 * @author Anton Utkaev
 * @since 2024.05.14
 */
public class ThemeSwitchButton extends Button {

    /**
     * Конструктор.
     */
    public ThemeSwitchButton() {
        super(VaadinIcon.ADJUST.create());

        addClassNames(LumoUtility.FontSize.XXSMALL);
        setWidth("min-content");
        getThemeNames().add(Lumo.DARK);

        addClickListener(event -> {
            if (event.getSource().hasThemeName(Lumo.DARK)) {
                event.getSource().getThemeNames().remove(Lumo.DARK);
                setTheme(null, false);
            } else {
                event.getSource().getThemeNames().add(Lumo.DARK);
                setTheme(Lumo.DARK, true);
            }
        });
    }

    /**
     * Установка темы.
     *
     * @param theme
     *            тема
     * @param set
     *            установить
     */
    private void setTheme(String theme, boolean set) {
        if (set) {
            UI.getCurrent().getPage().executeJs("document.documentElement.setAttribute('theme', '" + theme + "')");
        } else {
            UI.getCurrent().getPage().executeJs("document.documentElement.setAttribute('theme', '')");
        }
    }
}
