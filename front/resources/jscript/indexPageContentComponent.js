import { makeRequest } from './commonFunctions.js';
import { createDirectorCard } from './directorService.js';

export function indexPageContentComponent(directorsArray = null) {
    if (directorsArray) {
        $('body > .content').replaceWith('<div class="content main_content"></div>');
        for (let director of directorsArray) {
            createDirectorCard(director);
        }
        return;
    }

    makeRequest('http://localhost:8080/cinema/directors/').then(result => {
        $('body > .content').replaceWith('<div class="content main_content"></div>');
        for (let director of result.responseJSON) {
            createDirectorCard(director);
        }
    });
}