(function() {
    'use strict';

    angular
        .module('rentitApp')
        .controller('RateDetailController', RateDetailController);

    RateDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Rate'];

    function RateDetailController($scope, $rootScope, $stateParams, entity, Rate) {
        var vm = this;

        vm.rate = entity;

        var unsubscribe = $rootScope.$on('rentitApp:rateUpdate', function(event, result) {
            vm.rate = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
