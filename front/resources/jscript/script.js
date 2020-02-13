import { indexPageContentComponent } from './indexPageContentComponent.js';
import { headerComponent } from './headerComponent.js';
import { menuComponent } from './menuComponent.js';
import { footerComponent } from './footerComponent.js';

(() => {
    headerComponent();
    menuComponent();
    indexPageContentComponent();
    footerComponent();
})();