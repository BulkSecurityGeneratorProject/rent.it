(function() {
    'use strict';

    angular
        .module('rentitApp')
        .controller('CurrencyDetailController', CurrencyDetailController);

    CurrencyDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Currency'];

    function CurrencyDetailController($scope, $rootScope, $stateParams, entity, Currency) {
        var vm = this;

        vm.currency = entity;

        var unsubscribe = $rootScope.$on('rentitApp:currencyUpdate', function(event, result) {
            vm.currency = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
