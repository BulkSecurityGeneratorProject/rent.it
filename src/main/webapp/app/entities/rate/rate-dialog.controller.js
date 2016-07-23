(function() {
    'use strict';

    angular
        .module('rentitApp')
        .controller('RateDialogController', RateDialogController);

    RateDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Rate'];

    function RateDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Rate) {
        var vm = this;

        vm.rate = entity;
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
            if (vm.rate.id !== null) {
                Rate.update(vm.rate, onSaveSuccess, onSaveError);
            } else {
                Rate.save(vm.rate, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('rentitApp:rateUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
