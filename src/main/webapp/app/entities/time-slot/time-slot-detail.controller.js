(function() {
    'use strict';

    angular
        .module('rentitApp')
        .controller('TimeSlotDetailController', TimeSlotDetailController);

    TimeSlotDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'TimeSlot'];

    function TimeSlotDetailController($scope, $rootScope, $stateParams, entity, TimeSlot) {
        var vm = this;

        vm.timeSlot = entity;

        var unsubscribe = $rootScope.$on('rentitApp:timeSlotUpdate', function(event, result) {
            vm.timeSlot = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
