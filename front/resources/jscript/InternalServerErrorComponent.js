export function internalServerErrorComponent() {
    $('body > .content').replaceWith(`
    <div class="content empty">
        <p class="message server_error">на сервера во время выполнения запроса произошла ошибка.</p>
    </div>
    `);
}