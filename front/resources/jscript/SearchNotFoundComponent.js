export function SearchNotFoundComponent() {
    $('body > .content').replaceWith(`
    <div class="content empty">
        <p class="message not_found">по вашему запросу ничего не найдено.</p>
    </div>
    `);
}