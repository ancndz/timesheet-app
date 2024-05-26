package ru.ancndz.timeapp.ui;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

/**
 * Конфигурация оболочки приложения.
 *
 * @author Anton Utkaev
 * @since 2024.04.19
 */
@Push
@PWA(name = "Timesheet App", shortName = "Timesheet", themeColor = "#212e40")
@Theme(value = "app-custom", variant = Lumo.DARK)
public class AppShell implements AppShellConfigurator {
}
