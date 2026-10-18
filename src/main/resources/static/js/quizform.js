// Shared by createquiz.html and editquiz.html.
// Questions and options can be added/removed freely; input names are re-indexed
// by position so Spring binds contiguous lists (questions[i].options[j]).
(function () {
    const MIN_OPTIONS = 2;
    const DEFAULT_OPTIONS = 4;
    const container = document.getElementById('questions');

    function button(label, cls, handler) {
        const b = document.createElement('button');
        b.type = 'button';
        b.className = cls;
        b.textContent = label;
        b.addEventListener('click', handler);
        return b;
    }

    function optionItem() {
        const li = document.createElement('li');
        li.className = 'option';
        li.innerHTML = '<input type="text" class="o-text" required> ' +
            '<input type="radio" class="o-correct" required> Correct ';
        li.appendChild(button('Remove option', 'remove-option', function () {
            const ul = li.parentElement;
            if (ul.children.length <= MIN_OPTIONS) {
                alert('A question needs at least ' + MIN_OPTIONS + ' options.');
                return;
            }
            li.remove();
            reindex();
        }));
        return li;
    }

    function decorate(question) {
        const ul = question.querySelector('ul');
        ul.querySelectorAll('li').forEach(function (li) {
            li.classList.add('option');
            if (!li.querySelector('.remove-option')) {
                li.appendChild(button('Remove option', 'remove-option', function () {
                    if (ul.children.length <= MIN_OPTIONS) {
                        alert('A question needs at least ' + MIN_OPTIONS + ' options.');
                        return;
                    }
                    li.remove();
                    reindex();
                }));
            }
        });
        if (!question.querySelector('.add-option')) {
            question.appendChild(button('Add option', 'add-option', function () {
                ul.appendChild(optionItem());
                reindex();
            }));
        }
        if (!question.querySelector('.remove-question')) {
            question.appendChild(button('Delete question', 'remove-question', function () {
                if (container.querySelectorAll('.question').length <= 1) {
                    alert('A quiz needs at least one question.');
                    return;
                }
                question.remove();
                reindex();
            }));
        }
    }

    function newQuestion() {
        const q = document.createElement('div');
        q.className = 'question';
        q.innerHTML = '<label>Question:</label> <input type="text" class="q-text" required><br>' +
            '<label>Options:</label><ul></ul>';
        const ul = q.querySelector('ul');
        for (let i = 0; i < DEFAULT_OPTIONS; i++) {
            ul.appendChild(optionItem());
        }
        decorate(q);
        return q;
    }

    function reindex() {
        container.querySelectorAll('.question').forEach(function (q, qi) {
            q.querySelector('input[type=text]').name = 'questions[' + qi + '].text';
            q.querySelectorAll('li.option').forEach(function (li, oi) {
                li.querySelector('input[type=text]').name = 'questions[' + qi + '].options[' + oi + '].text';
                const radio = li.querySelector('input[type=radio]');
                radio.name = 'questions[' + qi + '].correctOption';
                radio.value = oi;
            });
        });
    }

    document.getElementById('add-question').addEventListener('click', function () {
        container.appendChild(newQuestion());
        reindex();
    });

    container.querySelectorAll('.question').forEach(decorate);
    reindex();
})();
