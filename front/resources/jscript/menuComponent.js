import { makeRequest } from './commonFunctions.js';
import { SearchNotFoundComponent } from './SearchNotFoundComponent.js';
import { indexPageContentComponent } from './indexPageContentComponent.js';
import { internalServerErrorComponent } from './InternalServerErrorComponent.js';

export function menuComponent() {
    let search = false;
    let searchStrategy;
    let NameOrIdWithDateStrategy;

    $('body > .menu').replaceWith(`
    <div class="menu">
        <div class="myui-checkbox disabled">
            <input type="checkbox" name="chbx-with-date" disabled/>
            <p class="myui-checkbox-label">with date</p>
        </div>
        ${datePickerElementMarkup()}
        <input type="text" name="inp-director_name" class="myui-form-control mg-left-10" placeholder="Имя режиссера либо id">
        <input type="text" name="inp-film_name" class="myui-form-control mg-left-10" placeholder="Название фильма">
        <button type="button" disabled name="btn-search" class="myui-form-control mg-left-10 disabled">Найти</button>
        <button type="button" name="btn-reset-filter" class="myui-form-control mg-left-10">Сбросить фильтр</button>
    </div>
    `);

    $('.menu > input[name="inp-director_name"]').on('input', function() {
        if ($(this).val() != '') {
            changeSearchStatus(true);
            if (!NameOrIdWithDateStrategy) {
                searchStrategy = () => {
                    let directorNameOrId = $(this).val();
                    while(directorNameOrId.includes(" ")) {
                        directorNameOrId = directorNameOrId.replace(" ", "-");
                    }
                    makeRequest('http://localhost:8080/cinema/directors/' + directorNameOrId).then(result => {
                        if (result.status === 200) {
                            if (result.responseJSON instanceof Array) {
                                indexPageContentComponent(result.responseJSON);
                            } else {
                                indexPageContentComponent([result.responseJSON]);
                            }
                        } else if (result.status === 404) {
                            SearchNotFoundComponent();
                        }
                    });
                };
            } else {
                searchDirectorNameOrIdWithDate();
            }

            if (NameOrIdWithDateStrategy) {
                $('.menu select[name="inp-month"]').prop("disabled", false);
                $('#month-picker').removeClass('disabled');
                $('.menu select[name="inp-year"]').prop("disabled", false);
                $('#year-picker').removeClass('disabled');
            } else {
                $('.menu select[name="inp-month"]').prop("disabled", true);
                $('#month-picker').addClass('disabled');
                $('.menu select[name="inp-year"]').prop("disabled", true);
                $('#year-picker').addClass('disabled');
            }

            $('.menu input[name="inp-film_name"]').prop("disabled", true);
            $('.menu input[name="inp-film_name"]').addClass('disabled');
            $('.menu input[name="chbx-with-date"]').prop('disabled', false);
            $('.menu .myui-checkbox').removeClass('disabled');
        } else {
            changeSearchStatus(false);
            searchStrategy = undefined;
            setNameOrDateStrategy(false);
            $('.menu input[name="chbx-with-date"]').prop('disabled', true);
            $('.menu input[name="chbx-with-date"]').prop('checked', false);
            $('.menu .myui-checkbox').addClass('disabled');
            $('.menu select[name="inp-month"]').prop("disabled", false);
            $('#month-picker').removeClass('disabled');
            $('.menu select[name="inp-year"]').prop("disabled", false);
            $('#year-picker').removeClass('disabled');
            $('.menu input[name="inp-film_name"]').prop("disabled", false);
            $('.menu input[name="inp-film_name"]').removeClass('disabled');
            $('.menu input[name="chbx-with-date"]').prop('disabled', true);
            $('.menu .myui-checkbox').addClass('disabled');
        }
    });

    $('.menu input[name="chbx-with-date"]').on('change', function() {
        if ($(this).prop('checked')) {
            setNameOrDateStrategy();
            searchDirectorNameOrIdWithDate();
        } else {
            setNameOrDateStrategy(false);
        }
    });

    $('.menu > input[name="inp-film_name"]').on('input', function() {
        if ($(this).val() != '') {
            changeSearchStatus(true);
            searchStrategy = () => {
                let filmName = $(this).val();
                while(filmName.includes(" ")) {
                    filmName = filmName.replace(" ", "-");
                }
                makeRequest('http://localhost:8080/cinema/films/' + filmName).then(result => {
                    if (result.status === 200) {
                        console.log(result.responseJSON);
                        indexPageContentComponent(result.responseJSON);
                    } else if (result.status === 404) {
                        SearchNotFoundComponent();
                    }
                });
            };

            $('.menu select[name="inp-month"]').prop("disabled", true);
            $('#month-picker').addClass('disabled');
            $('.menu select[name="inp-year"]').prop("disabled", true);
            $('#year-picker').addClass('disabled');
            $('.menu input[name="inp-director_name"]').prop("disabled", true);
            $('.menu input[name="inp-director_name"]').addClass('disabled');
        } else {
            changeSearchStatus(false);
            $('.menu select[name="inp-month"]').prop("disabled", false);
            $('#month-picker').removeClass('disabled');
            $('.menu select[name="inp-year"]').prop("disabled", false);
            $('#year-picker').removeClass('disabled');
            $('.menu input[name="inp-director_name"]').prop("disabled", false);
            $('.menu input[name="inp-director_name"]').removeClass('disabled');
        }
    });

    $('.menu .myui-picker select').on('change', () => {
        if (NameOrIdWithDateStrategy) {
            searchDirectorNameOrIdWithDate();
        } else {
            changeSearchStatus(true);
            searchStrategy = () => {
            let month = $('#month-picker select').val();
            let year = $('#year-picker select').val();
            makeRequest('http://localhost:8080/cinema/films/' + year + "-" + month + "-00").then(result => {
                if (result.status === 200) {
                    indexPageContentComponent(result.responseJSON);
                } else if (result.status === 404) {
                    SearchNotFoundComponent();
                } else if (result.status === 500) {
                    internalServerErrorComponent();
                }
            });
        };

        $('.menu input[name="inp-director_name"]').prop("disabled", true);
        $('.menu input[name="inp-director_name"]').addClass('disabled');
        $('.menu input[name="inp-film_name"]').prop("disabled", true);
        $('.menu input[name="inp-film_name"]').addClass('disabled');
        }
    });

    $('.menu > button[name="btn-search"]').on('click', () => {
        searchStrategy();
    });

    $('.menu button[name="btn-reset-filter"]').on('click', () => {
        changeSearchStatus(false);
        searchStrategy = undefined;
        setNameOrDateStrategy(false);

        $('.menu input[name="chbx-with-date"]').prop('disabled', true);
        $('.menu input[name="chbx-with-date"]').prop('checked', false);
        $('.menu .myui-checkbox').addClass('disabled');
        $('.menu input[name="inp-director_name"]').val('');
        $('.menu input[name="inp-film_name"]').val('');
        $('#month-picker select').val($('#month-picker option:first').val());
        $('#year-picker select').val($('#year-picker option:first').val());
        $('#month-picker').removeClass('disabled');
        $('#month-picker select').prop('disabled', false);
        $('#year-picker').removeClass('disabled');
        $('#year-picker select').prop('disabled', false);
        $('.menu input[name="inp-director_name"]').removeClass('disabled');
        $('.menu input[name="inp-director_name"]').prop('disabled', false);
        $('.menu input[name="inp-film_name"]').removeClass('disabled');
        $('.menu input[name="inp-film_name"]').prop('disabled', false);

        indexPageContentComponent();
    });

    function changeSearchStatus(newStatus) {
        search = newStatus;
    
        if (search === true) {
            $('.menu button[name="btn-search"]').prop('disabled', false);
            $('.menu button[name="btn-search"]').removeClass('disabled');
        } else {
            $('.menu button[name="btn-search"]').prop('disabled', true);
            $('.menu button[name="btn-search"]').addClass('disabled');
        }
    }

    function setNameOrDateStrategy(set = true) {
        if (set) {
            NameOrIdWithDateStrategy = true;            
            $('#month-picker').removeClass('disabled');
            $('#month-picker select').prop('disabled', false);
            $('#year-picker').removeClass('disabled');
            $('#year-picker select').prop('disabled', false);
        } else {
            NameOrIdWithDateStrategy = false;
            $('#month-picker select').val($('#month-picker option:first').val());
            $('#year-picker select').val($('#year-picker option:first').val());
            $('.menu select[name="inp-month"]').prop("disabled", true);
            $('#month-picker').addClass('disabled');
            $('.menu select[name="inp-year"]').prop("disabled", true);
            $('#year-picker').addClass('disabled');
        }
    }

    function searchDirectorNameOrIdWithDate() {
        searchStrategy = () => {
            console.log('with date and name');
            let directorNameOrId = $('.menu input[name="inp-director_name"]').val();
            let year = $('#year-picker select').val();
            let month = $('#month-picker select').val();
    
            while(directorNameOrId.includes(" ")) {
                directorNameOrId = directorNameOrId.replace(" ", "-");
            }
    
            makeRequest(`http://localhost:8080/cinema/directors/${directorNameOrId}/${year}-${month}-00`).then(result => {
                if (result.status === 200) {
                    if (result.responseJSON instanceof Array) {
                        indexPageContentComponent(result.responseJSON);
                    } else {
                        indexPageContentComponent([result.responseJSON]);
                    }
                } else if (result.status === 404) {
                    SearchNotFoundComponent();
                } else if (result.status === 500) {
                    internalServerErrorComponent();
                }
            });
        };
    }
}

function datePickerElementMarkup() {
    return `
    <div class="myui-picker mg-left-10" id="month-picker">
        <span class="myui-picker-label">МЕСЯЦ</span>
        <select name="inp-month">
            <option value="01">Январь</option>
            <option value="02">Февраль</option>
            <option value="03">Март</option>
            <option value="04">Апрель</option>
            <option value="05">Май</option>
            <option value="06">Июнь</option>
            <option value="07">Июль</option>
            <option value="08">Август</option>
            <option value="09">Сентябрь</option>
            <option value="10">Октябрь</option>
            <option value="11">Ноябрь</option>
            <option value="12">Декабрь</option>
        </select>
    </div>
    <div class="myui-picker mg-left-10" id="year-picker">
        <span class="myui-picker-label">ГОД</span>
        <select name="inp-year">
            ${(function() {
                let result = '';
                for (let i = 1895; i <= 2030; i++) {
                    result += `<option value=${i}>${i}</option>`;
                }
                return result;
            })()}
        </select>
    </div>
    `;
}