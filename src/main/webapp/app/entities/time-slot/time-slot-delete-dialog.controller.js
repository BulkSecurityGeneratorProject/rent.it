(function() {
    'use strict';

    angular
        .module('rentitApp')
        .controller('TimeSlotDeleteController',TimeSlotDeleteController);

    TimeSlotDeleteController.$inject = ['$uibModalInstance', 'entity', 'TimeSlot'];

    function TimeSlotDeleteController($uibModalInstance, entity, TimeSlot) {
        var vm = this;

        vm.timeSlot = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            TimeSlot.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
