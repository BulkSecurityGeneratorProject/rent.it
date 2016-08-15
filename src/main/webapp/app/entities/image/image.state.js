(function() {
    'use strict';

    angular
        .module('rentitApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('image', {
            parent: 'entity',
            url: '/image?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'rentitApp.image.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/image/images.html',
                    controller: 'ImageController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('image');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('image-detail', {
            parent: 'entity',
            url: '/image/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'rentitApp.image.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/image/image-detail.html',
                    controller: 'ImageDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('image');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Image', function($stateParams, Image) {
                    return Image.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('image.new', {
            parent: 'image',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/image/image-dialog.html',
                    controller: 'ImageDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                url: null,
                                type: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('image', null, { reload: true });
                }, function() {
                    $state.go('image');
                });
            }]
        })
        .state('image.edit', {
            parent: 'image',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/image/image-dialog.html',
                    controller: 'ImageDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Image', function(Image) {
                            return Image.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('image', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('image.delete', {
            parent: 'image',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/image/image-delete-dialog.html',
                    controller: 'ImageDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Image', function(Image) {
                            return Image.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('image', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
