export function headerComponent() {
    $('body > .header').replaceWith(`
    <div class="header">
        <div class="logo">
            <span>Cinema</span>
            <span class="sublogo">: by Vladislav Soldatenkov</span>
        </div>
    </div>
    `);
}