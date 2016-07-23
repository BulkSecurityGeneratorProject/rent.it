(function() {
    'use strict';

    angular
        .module('rentitApp')
        .controller('TimeSlotDialogController', TimeSlotDialogController);

    TimeSlotDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'TimeSlot'];

    function TimeSlotDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, TimeSlot) {
        var vm = this;

        vm.timeSlot = entity;
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
            if (vm.timeSlot.id !== null) {
                TimeSlot.update(vm.timeSlot, onSaveSuccess, onSaveError);
            } else {
                TimeSlot.save(vm.timeSlot, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('rentitApp:timeSlotUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
