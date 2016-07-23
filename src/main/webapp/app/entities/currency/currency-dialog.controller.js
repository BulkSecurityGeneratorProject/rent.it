(function() {
    'use strict';

    angular
        .module('rentitApp')
        .controller('CurrencyDialogController', CurrencyDialogController);

    CurrencyDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Currency'];

    function CurrencyDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Currency) {
        var vm = this;

        vm.currency = entity;
        vm.clear = clear;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.currency.id !== null) {
                Currency.update(vm.currency, onSaveSuccess, onSaveError);
            } else {
                Currency.save(vm.currency, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('rentitApp:currencyUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
