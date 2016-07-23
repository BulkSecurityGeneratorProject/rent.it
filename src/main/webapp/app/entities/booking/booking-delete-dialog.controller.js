(function() {
    'use strict';

    angular
        .module('rentitApp')
        .controller('BookingDeleteController',BookingDeleteController);

    BookingDeleteController.$inject = ['$uibModalInstance', 'entity', 'Booking'];

    function BookingDeleteController($uibModalInstance, entity, Booking) {
        var vm = this;

        vm.booking = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Booking.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
