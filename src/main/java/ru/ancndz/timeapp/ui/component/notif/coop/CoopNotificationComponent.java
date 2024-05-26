package ru.ancndz.timeapp.ui.component.notif.coop;

import ru.ancndz.timeapp.ui.component.notif.CommonNotificationComponent;

/**
 * Компонент уведомления о сотрудничестве.
 *
 * @author Anton Utkaev
 * @since 2024.05.19
 */
public class CoopNotificationComponent extends CommonNotificationComponent {

    public static class CoopBuilder extends AbstractBuilder<CoopNotificationComponent> {

        public static CoopBuilder newBuilder() {
            return new CoopBuilder();
        }

        @Override
        protected CoopNotificationComponent createEmpty() {
            return new CoopNotificationComponent();
        }

        @Override
        public CoopNotificationComponent build() {
            return super.build();
        }
    }

}
