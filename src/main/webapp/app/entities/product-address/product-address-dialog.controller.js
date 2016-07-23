(function() {
    'use strict';

    angular
        .module('rentitApp')
        .controller('ProductAddressDialogController', ProductAddressDialogController);

    ProductAddressDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'ProductAddress'];

    function ProductAddressDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, ProductAddress) {
        var vm = this;

        vm.productAddress = entity;
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
            if (vm.productAddress.id !== null) {
                ProductAddress.update(vm.productAddress, onSaveSuccess, onSaveError);
            } else {
                ProductAddress.save(vm.productAddress, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('rentitApp:productAddressUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
