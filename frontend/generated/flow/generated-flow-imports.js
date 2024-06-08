import '@vaadin/side-nav/src/vaadin-side-nav.js';
import '@vaadin/polymer-legacy-adapter/style-modules.js';
import '@vaadin/app-layout/src/vaadin-app-layout.js';
import '@vaadin/tooltip/src/vaadin-tooltip.js';
import '@vaadin/button/src/vaadin-button.js';
import 'Frontend/generated/jar-resources/buttonFunctions.js';
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/horizontal-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/icon/src/vaadin-icon.js';
import '@vaadin/side-nav/src/vaadin-side-nav-item.js';
import '@vaadin/icons/vaadin-iconset.js';
import '@vaadin/app-layout/src/vaadin-drawer-toggle.js';
import '@vaadin/scroller/src/vaadin-scroller.js';
import '@vaadin/login/src/vaadin-login-overlay.js';
import '@vaadin/common-frontend/ConnectionIndicator.js';
import '@vaadin/vaadin-lumo-styles/color-global.js';
import '@vaadin/vaadin-lumo-styles/typography-global.js';
import '@vaadin/vaadin-lumo-styles/sizing.js';
import '@vaadin/vaadin-lumo-styles/spacing.js';
import '@vaadin/vaadin-lumo-styles/style.js';
import '@vaadin/vaadin-lumo-styles/vaadin-iconset.js';

const loadOnDemand = (key) => {
  const pending = [];
  if (key === '2fb911c80bf7da79f7e8d1eabb730f8639f1dbf65e9049b6139810af6193a0e6') {
    pending.push(import('./chunks/chunk-153b5edaae9bd65eed2ceaeb8158f83f88517c337e9829fd172ef7b07e38709c.js'));
  }
  if (key === 'd11143ad1041787d3271468a6c646cbc09f0b1ac25d24869e4a2039c2fe6011b') {
    pending.push(import('./chunks/chunk-9267ae5db04f2676d7c9e6de0723bcf7cd4f22cc7649165b2ed67a1a5a7e0fad.js'));
  }
  if (key === '38cdfaef6797f9a8ec4e4dadff38c482936363f040d36432d86b9a0587a994bd') {
    pending.push(import('./chunks/chunk-24c0f4a2f98624e2985c36e6ef90c2ceff96a8f8b5f95bc6193843d14f5b1eb9.js'));
  }
  if (key === 'bd4b2911797e2840a1a5dad058a80fa02a88244d5f56739cdd192117c3b226a2') {
    pending.push(import('./chunks/chunk-f4089a7971092f2ccc14b34e32afd1d2484a19c7dd82eafb5b407a83cba8c585.js'));
  }
  if (key === '1999cd5da8115dd2777b0784622d0c12cb8ad53ba36ecac013793b850441b207') {
    pending.push(import('./chunks/chunk-ca6bc9c56aa20f953f1d0630ceb76b59760d8cf3a6d6a7aaf3afbd8638b61bab.js'));
  }
  if (key === '31ba9eacb227af4520a776d2caf39b0dd2631ab3d74b189037a83b5701f38aa4') {
    pending.push(import('./chunks/chunk-f214549dbc3a93d637a929c16643506080421f8351c7638bc6121d67eb76e302.js'));
  }
  if (key === '31b7888ee654b27ee353cbc459feb8c519967c582b8c3c2a60266b1793334a5f') {
    pending.push(import('./chunks/chunk-285a4427d107d524c9009b9d26d3729677cf3000a5e1ec88b6d279fea7843e06.js'));
  }
  return Promise.all(pending);
}

window.Vaadin = window.Vaadin || {};
window.Vaadin.Flow = window.Vaadin.Flow || {};
window.Vaadin.Flow.loadOnDemand = loadOnDemand;
window.Vaadin.Flow.resetFocus = () => {
 let ae=document.activeElement;
 while(ae&&ae.shadowRoot) ae = ae.shadowRoot.activeElement;
 return !ae || ae.blur() || ae.focus() || true;
}