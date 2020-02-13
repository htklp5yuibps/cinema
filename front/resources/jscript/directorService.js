import { Director } from './entity/director.js';
import { makeRequest } from './commonFunctions.js';

export function getDirectors() {
    return makeRequest('http://localhost:8080/cinema').then(result => {
        switch (result.status) {
            case '200': {
                let directors = [];

                for (let object in result.responseJSON) {
                    directors.push(new Director(object.firstName, object.lastName, object.dob));
                }

                return directors;
            }
            default: {
                return null;
            }
        }
    });
}

export function createDirectorCard(director) {
    $('.content.main_content').append(`
    <div class="director_card">
        <div class="director_card_name">${director.firstName} ${director.lastName} ${director.dob}</div>
        <div class="director_card_content">
        ${filmographyElement()}
        </div>
    </div>
    `);

    function filmographyElement() {
        let elementLayout = '<div class="director_card_filmography"><div class="title">Фильмография</div>';
        for (let film of director.films) {
            elementLayout += `<div class="film">${film.name}, ${film.genre}, ${film.releaseDate}</div>`;
        }
        elementLayout += '</div>';
        return elementLayout;
    }
}