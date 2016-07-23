(function() {
    'use strict';

    angular
        .module('rentitApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('time-slot', {
            parent: 'entity',
            url: '/time-slot',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'rentitApp.timeSlot.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/time-slot/time-slots.html',
                    controller: 'TimeSlotController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('timeSlot');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('time-slot-detail', {
            parent: 'entity',
            url: '/time-slot/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'rentitApp.timeSlot.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/time-slot/time-slot-detail.html',
                    controller: 'TimeSlotDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('timeSlot');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'TimeSlot', function($stateParams, TimeSlot) {
                    return TimeSlot.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('time-slot.new', {
            parent: 'time-slot',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/time-slot/time-slot-dialog.html',
                    controller: 'TimeSlotDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                dateTime: null,
                                duration: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('time-slot', null, { reload: true });
                }, function() {
                    $state.go('time-slot');
                });
            }]
        })
        .state('time-slot.edit', {
            parent: 'time-slot',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/time-slot/time-slot-dialog.html',
                    controller: 'TimeSlotDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['TimeSlot', function(TimeSlot) {
                            return TimeSlot.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('time-slot', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('time-slot.delete', {
            parent: 'time-slot',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/time-slot/time-slot-delete-dialog.html',
                    controller: 'TimeSlotDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['TimeSlot', function(TimeSlot) {
                            return TimeSlot.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('time-slot', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
