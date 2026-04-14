(function () {
    const DEFAULT_LOCALE = 'en-us';
    const SUPPORTED_LOCALES = [DEFAULT_LOCALE, 'pt-br'];
    const LOCALE_META = {
        'en-us': {
            htmlLang: 'en-US',
            appName: 'HUB Employees API',
            switchLabel: 'Language',
            searchPlaceholder: 'Search internal docs',
            noData: 'No matching page found',
            options: {
                'en-us': 'English (US)',
                'pt-br': 'Portuguese (Brazil)'
            }
        },
        'pt-br': {
            htmlLang: 'pt-BR',
            appName: 'HUB Employees API',
            switchLabel: 'Idioma',
            searchPlaceholder: 'Buscar na documentação',
            noData: 'Nenhuma página encontrada',
            options: {
                'en-us': 'Inglês (EUA)',
                'pt-br': 'Português (Brasil)'
            }
        }
    };

    function getHashState() {
        const raw = decodeURI(window.location.hash.replace(/^#\/?/, '').replace(/^\/+/, '').trim());
        const [pathPart, queryPart] = raw.split('?');

        return {
            path: (pathPart || '').replace(/^\/+|\/+$/g, ''),
            query: queryPart ? '?' + queryPart : ''
        };
    }

    function getHashPath() {
        return getHashState().path;
    }

    function hasExplicitLocale(path) {
        const segment = path.split('/')[0].toLowerCase();
        return SUPPORTED_LOCALES.includes(segment);
    }

    function detectLocale(path) {
        return hasExplicitLocale(path) ? path.split('/')[0].toLowerCase() : DEFAULT_LOCALE;
    }

    function stripLocale(path) {
        if (!hasExplicitLocale(path)) {
            return path.replace(/^\/+|\/+$/g, '');
        }

        return path
            .split('/')
            .slice(1)
            .join('/')
            .replace(/^\/+|\/+$/g, '');
    }

    function buildHash(locale, pathWithoutLocale, querySuffix) {
        const suffix = (pathWithoutLocale || '').replace(/^\/+|\/+$/g, '');
        const query = querySuffix || '';

        if (locale === DEFAULT_LOCALE) {
            return (suffix ? '#/' + suffix : '#/') + query;
        }

        return (suffix ? '#/' + locale + '/' + suffix : '#/' + locale + '/') + query;
    }

    function getPreferredLocale() {
        try {
            const stored = window.localStorage.getItem('docs-locale');
            return SUPPORTED_LOCALES.includes(stored) ? stored : DEFAULT_LOCALE;
        } catch (_error) {
            return DEFAULT_LOCALE;
        }
    }

    function setPreferredLocale(locale) {
        try {
            window.localStorage.setItem('docs-locale', locale);
        } catch (_error) {
            // Ignore storage failures in restrictive browser contexts.
        }
    }

    function syncDocumentLanguage(locale) {
        document.documentElement.lang = LOCALE_META[locale].htmlLang;
    }

    function normalizeLocaleRoute() {
        const hashState = getHashState();
        const path = hashState.path;

        if (hasExplicitLocale(path)) {
            const locale = detectLocale(path);
            setPreferredLocale(locale);
            syncDocumentLanguage(locale);
            return false;
        }

        const preferredLocale = getPreferredLocale();
        syncDocumentLanguage(DEFAULT_LOCALE);

        if (preferredLocale !== DEFAULT_LOCALE) {
            const target = buildHash(preferredLocale, path, hashState.query);
            if (window.location.hash !== target) {
                window.location.replace(target);
                return true;
            }
        }

        return false;
    }

    function ensureLanguageSwitcher(locale) {
        const nav = document.querySelector('.app-nav');
        if (!nav) {
            return;
        }

        let container = nav.querySelector('.docsify-language-switcher');
        let label;
        let select;

        if (!container) {
            container = document.createElement('div');
            container.className = 'docsify-language-switcher';

            label = document.createElement('label');
            label.className = 'docsify-language-switcher__label';
            label.htmlFor = 'docsify-language-select';

            select = document.createElement('select');
            select.id = 'docsify-language-select';
            select.className = 'docsify-language-switcher__select';
            select.addEventListener('change', function (event) {
                const targetLocale = event.target.value;
                const hashState = getHashState();

                setPreferredLocale(targetLocale);
                const targetHash = buildHash(
                    targetLocale,
                    stripLocale(hashState.path),
                    hashState.query
                );

                if (window.location.hash !== targetHash) {
                    window.location.hash = targetHash;
                }
            });

            container.appendChild(label);
            container.appendChild(select);
            nav.appendChild(container);
        } else {
            label = container.querySelector('.docsify-language-switcher__label');
            select = container.querySelector('.docsify-language-switcher__select');
        }

        label.textContent = LOCALE_META[locale].switchLabel;
        select.innerHTML = '';

        SUPPORTED_LOCALES.forEach(function (code) {
            const option = document.createElement('option');
            option.value = code;
            option.textContent = LOCALE_META[locale].options[code];
            if (code === locale) {
                option.selected = true;
            }
            select.appendChild(option);
        });
    }

    function ensureAppNameLink(locale) {
        const appNameLink = document.querySelector('.app-name-link');
        if (!appNameLink) {
            return;
        }

        const targetHash = buildHash(locale, '');
        appNameLink.textContent = LOCALE_META[locale].appName;
        appNameLink.setAttribute('href', targetHash);

        if (appNameLink.dataset.localeBound !== 'true') {
            appNameLink.addEventListener('click', function (event) {
                event.preventDefault();

                const currentLocale = getPreferredLocale();
                const homeHash = buildHash(currentLocale, '');

                if (window.location.hash === homeHash) {
                    window.location.replace(homeHash);
                    requestAnimationFrame(syncPortalUi);
                    return;
                }

                window.location.hash = homeHash;
            });

            appNameLink.dataset.localeBound = 'true';
        }
    }

    function localizeSearch(locale) {
        const searchInput = document.querySelector('.sidebar .search input');
        if (searchInput) {
            searchInput.placeholder = LOCALE_META[locale].searchPlaceholder;
            searchInput.setAttribute('aria-label', LOCALE_META[locale].searchPlaceholder);
        }

        const emptyState = document.querySelector('.sidebar .results-panel .empty');
        if (emptyState) {
            emptyState.textContent = LOCALE_META[locale].noData;
        }
    }

    function syncPortalUi() {
        const path = getHashPath();
        const locale = detectLocale(path);

        if (hasExplicitLocale(path)) {
            setPreferredLocale(locale);
        }

        syncDocumentLanguage(locale);
        ensureAppNameLink(locale);
        ensureLanguageSwitcher(locale);
        localizeSearch(locale);
    }

    if (normalizeLocaleRoute()) {
        return;
    }

    const initialLocale = detectLocale(getHashPath());

    window.addEventListener('hashchange', function () {
        if (normalizeLocaleRoute()) {
            return;
        }

        requestAnimationFrame(syncPortalUi);
    });

    window.$docsify = {
        name: LOCALE_META[initialLocale].appName,
        loadSidebar: true,
        loadNavbar: true,
        coverpage: true,
        onlyCover: false,
        auto2top: true,
        relativePath: true,
        subMaxLevel: 3,
        maxLevel: 3,
        alias: {
            '/pt-br/_sidebar.md': '/pt-br/_sidebar.md',
            '/pt-br/_navbar.md': '/pt-br/_navbar.md',
            '/pt-br/_coverpage.md': '/pt-br/_coverpage.md',
            '/pt-br/.*/_sidebar.md': '/pt-br/_sidebar.md',
            '/pt-br/.*/_navbar.md': '/pt-br/_navbar.md',
            '/pt-br/.*/_coverpage.md': '/pt-br/_coverpage.md',
            '/.*/_sidebar.md': '/_sidebar.md',
            '/.*/_navbar.md': '/_navbar.md',
            '/.*/_coverpage.md': '/_coverpage.md'
        },
        search: {
            paths: 'auto',
            depth: 4,
            placeholder: LOCALE_META[initialLocale].searchPlaceholder,
            noData: LOCALE_META[initialLocale].noData,
            hideOtherSidebarContent: false
        },
        plugins: [
            function (hook, _vm) {
                hook.mounted(function () {
                    requestAnimationFrame(syncPortalUi);
                });

                hook.doneEach(function () {
                    requestAnimationFrame(syncPortalUi);
                });
            }
        ]
    };
})();
