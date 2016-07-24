(function() {
    'use strict';

    angular
        .module('rentitApp')
        .controller('MessageDetailController', MessageDetailController);

    MessageDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Message', 'User'];

    function MessageDetailController($scope, $rootScope, $stateParams, entity, Message, User) {
        var vm = this;

        vm.message = entity;

        var unsubscribe = $rootScope.$on('rentitApp:messageUpdate', function(event, result) {
            vm.message = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
